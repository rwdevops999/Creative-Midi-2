package util.keyboards.yamaha;

import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.KbFolder;
import util.keyboards.yamaha.entity.Keyboard;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileTransferBuilder {

    private static final int RAW_BYTES_PER_PACKET = 210; // 30 groepen van 7 bytes = 240 gecodeerde bytes

    // Verwachte ACK van het keyboard na elk pakket: F0 43 50 00 03 00 F7
    public static final byte[] EXPECTED_ACK = new byte[] {
            (byte) 0xF0, 0x43, 0x50, 0x00, 0x03, 0x00, (byte) 0xF7
    };

    /**
     * Create a list with SysEx-messages for a complete data transfer:
     * Index 0 .. N         = Data packets (02 02 xx)
     *
     * @param rawFileData   De complete byte[] content of the file
     * @return              List of all SysEx-messages to be sent one by one (after each ACK)
     */
    public static List<byte[]> buildDataPackets(byte[] rawFileData) {
        List<byte[]> allMessages = new ArrayList<>();
        if (rawFileData == null || rawFileData.length == 0) {
            return allMessages;
        }

        // Data packets
        int totalPackets = (rawFileData.length + RAW_BYTES_PER_PACKET - 1) / RAW_BYTES_PER_PACKET;
        for (int packetIndex = 0; packetIndex < totalPackets; packetIndex++) {
            int start = packetIndex * RAW_BYTES_PER_PACKET;
            int end = Math.min(start + RAW_BYTES_PER_PACKET, rawFileData.length);
            byte[] rawChunk = Arrays.copyOfRange(rawFileData, start, end);
            allMessages.add(buildDataPacket(packetIndex, encode8to7(rawChunk)));
        }

        return allMessages;
    }

    /**
     * Build the file message: F0 43 50 00 06 01 ... F7
     */
    public static byte[] packedDestination(Keyboard kb, String drive, String folder, String filename, int fileSize) {

        System.out.println("packedDestination filename = [" + filename + "]");

        if (filename == null ||
                !filename.matches("(?i)[A-Za-z0-9_. -]+\\.(sty|mid)")) {
            throw new IllegalArgumentException(
                    "Invalid filename: [" + filename + "]"
            );
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        KbDrive kbDrive = kb.getDrives().stream().filter(d -> drive.equals(d.getName())).findFirst().orElse(null);
        if (kbDrive != null) {
            KbFolder kbFolder = kbDrive.getFolders().stream().filter(f -> folder.equals(f.getName())).findFirst().orElse(null);
            if (kbFolder != null) {
                String filePath = (kbDrive.getId() + ":\\" + kbFolder.getName() + "\\" + filename);
                return buildFileOpenPacket(filePath, fileSize);
            }
        }

        return new byte[0];
    }


    // 2. Daarna het 48-byte File Open pakket (met 00 05 00 + 4 grootte-bytes)
    private static byte[] buildFileOpenPacket(String targetPath, int rawFileSize) {
        byte[] pathWithNull = (targetPath + "\0").getBytes(StandardCharsets.US_ASCII);
        byte[] encodedPath = encode8to7(pathWithNull);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0xF0);
        out.write(0x43);
        out.write(0x50);
        out.write(0x00);
        out.write(0x06);
        out.write(0x01);

        out.write(0x00);
        out.write(0x05);
        out.write(0x00); // De 5e nul-byte zoals in 00 05 00 00 03 11 3C
        out.write((rawFileSize >> 21) & 0x7F);
        out.write((rawFileSize >> 14) & 0x7F);
        out.write((rawFileSize >> 7) & 0x7F);
        out.write(rawFileSize & 0x7F);

        byte[] timestamp = { 0x0A, 0x63, 0x18, 0x7E, 0x20, 0x0C, 0x58, 0x6D, 0x09, 0x40, 0x25 };
        out.writeBytes(timestamp);

        out.write((encodedPath.length >> 7) & 0x7F);
        out.write(encodedPath.length & 0x7F);
        out.writeBytes(encodedPath);

        out.write(0xF7);
        return out.toByteArray();
    }

    private static byte[] buildDataPacket(int packetNumber, byte[] encodedData) {
        int bodyLength = 3 + encodedData.length; // 3 sub-header bytes + data
        byte[] packet = new byte[6 + bodyLength + 2];

        // Vaste Yamaha header
        packet[0] = (byte) 0xF0;
        packet[1] = 0x43;
        packet[2] = 0x50;
        packet[3] = 0x01;

        // 7-bits lengte van de body (bijv. 243 -> 0x01, 0x73)
        packet[4] = (byte) ((bodyLength >> 7) & 0x7F);
        packet[5] = (byte) (bodyLength & 0x7F);

        // Sub-header (telt mee voor lengte en checksum)
        packet[6] = 0x02;
        packet[7] = 0x02;
        packet[8] = (byte) (packetNumber & 0x7F); // Rolt automatisch terug naar 0 na 127

        // Gecodeerde data kopiëren
        System.arraycopy(encodedData, 0, packet, 9, encodedData.length);

        // Checksum berekenen over index 6 t/m einde van encodedData
        int sum = 0;
        for (int i = 6; i < 6 + bodyLength; i++) {
            sum += packet[i];
        }
        packet[6 + bodyLength] = (byte) ((-sum) & 0x7F);

        // Einde SysEx
        packet[6 + bodyLength + 1] = (byte) 0xF7;

        return packet;
    }

    /**
     * Bouwt het standaard sluitbericht na het laatste datapakket: F0 43 50 00 06 02 F7
     */
    public static byte[] buildFileClosePacket() {
        return new byte[] {
                (byte) 0xF0, 0x43, 0x50, 0x01, 0x00, 0x02, 0x02, 0x03, 0x7B, (byte) 0xF7
        };
    }
    /**
     * Yamaha 7-in-8 bit-packing: zet elke 7 ruwe bytes om in 8 7-bits bytes.
     */
    public static byte[] encode8to7(byte[] raw) {
        int fullGroups = raw.length / 7;
        int remainder = raw.length % 7;
        int encodedLength = (fullGroups * 8) + (remainder > 0 ? remainder + 1 : 0);

        byte[] encoded = new byte[encodedLength];
        int rawPos = 0;
        int encPos = 0;

        while (rawPos < raw.length) {
            int groupSize = Math.min(7, raw.length - rawPos);
            byte highBits = 0;

            for (int i = 0; i < groupSize; i++) {
                int b = raw[rawPos + i] & 0xFF;
                if ((b & 0x80) != 0) {
                    highBits |= (1 << (6 - i));
                }
                encoded[encPos + 1 + i] = (byte) (b & 0x7F);
            }

            encoded[encPos] = highBits;
            rawPos += groupSize;
            encPos += groupSize + 1;
        }

        return encoded;
    }
}