package util.keyboards.yamaha;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.KbFile;
import util.keyboards.yamaha.entity.KbFolder;
import util.keyboards.yamaha.entity.Keyboard;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static util.keyboards.yamaha.General.asHex;

public class FilePackets {
    private static final Logger logger = LoggerFactory.getLogger(FilePackets.class);

    public static final int CHUNK_SIZE = 210;

    /** Yamaha 7-bit data-packing: one MSB mask then up to seven low-7-bit bytes. */
    private static byte[] packData(byte[] data, int offset, int length) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int p = offset; p < offset + length; p += 7) {
            int count = Math.min(7, offset + length - p);
            int mask = 0;
            for (int j = 0; j < count; j++)
                if ((data[p + j] & 0x80) != 0) mask |= 1 << (6 - j);
            out.write(mask);
            for (int j = 0; j < count; j++) out.write(data[p + j] & 0x7f);
        }
        return out.toByteArray();
    }

    public static List<byte[]> buildDataPackets(byte[] fileBytes) {

        if (fileBytes == null || fileBytes.length == 0)
            throw new IllegalArgumentException("Empty style file");

        int count = (fileBytes.length + CHUNK_SIZE - 1) / CHUNK_SIZE;

/*        if (count > 128)
            throw new IllegalArgumentException(
                    "More than 128 packets: sequence extension unverified"
            );
*/
        List<byte[]> packets = new ArrayList<>(count);

        for (int seq = 0; seq < count; seq++) {

            int start = seq * CHUNK_SIZE;
            int inputLength = Math.min(
                    CHUNK_SIZE,
                    fileBytes.length - start
            );

            byte[] packed = packData(fileBytes, start, inputLength);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write(0xF0);
            out.write(0x43);
            out.write(0x50);
            out.write(0x01);

            // TEST: final-packet flag; not yet confirmed for PC -> PSR
            out.write(0x01);
//            out.write(seq == count - 1 ? 0x00 : 0x01);

            out.write((packed.length + 3) & 0x7F);
            out.write(0x02);
            out.write(0x02);
            int sequence = seq & 0x7F;
            out.write(sequence);
//            out.write(seq);

            // Actual packed file data
            out.writeBytes(packed);

// Checksum
            int sum = sequence;

            for (byte b : packed)
                sum += b & 0xFF;

            out.write((124 - sum) & 0x7F);

            // End of SysEx
            out.write(0xF7);

            byte[] packet = out.toByteArray();
            logger.debug("SEND PACKET: {} = {}",
                    seq,
                    java.util.HexFormat.of().formatHex(packet)
            );
            packets.add(packet);
        }

        return packets;
    }

    public static List<byte[]> buildDataPackets(Path styleFile) throws java.io.IOException {
        return buildDataPackets(Files.readAllBytes(styleFile));
    }

    /** Only ASCII destination names are currently supported. */
    private static byte[] packedDestination(Keyboard kb, String drive, String folder, String filename) {

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
                byte[] raw = (kbDrive.getId() + ":\\" + kbFolder.getName() + "\\" + filename)
                        .getBytes(StandardCharsets.US_ASCII);

                for (int i = 0; i < raw.length; i++) {
                    if (i > 0 && i % 7 == 0) {
                        out.write(0);
                    }
                    out.write(raw[i]);
                }

                out.write(0);

                return out.toByteArray();
            }
        }

        return new byte[0];
    }

    public static byte[] buildTransferRequest(Keyboard kb, String drive, String folder, String filename, long size) {
        if (size <= 0 || size > 0x7ffffffffL)
            throw new IllegalArgumentException("Unsupported file size");

        byte[] path = packedDestination(kb, drive, folder, filename);
        if (path.length > 127) {
            throw new IllegalArgumentException("Destination path too long");
        }

        if (path.length > 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.writeBytes(new byte[]{(byte) 0xF0, 0x43, 0x50, 0x00, 0x06, 0x01, 0x00, 0x05});
            for (int shift = 28; shift >= 0; shift -= 7) out.write((int) (size >>> shift) & 0x7F);
            out.writeBytes(new byte[]{0x05, 0, 0, 0, 0, 0, 0});
            out.write(path.length + 1);
            out.write(0x00);
            out.writeBytes(path);
            out.write(0xF7);
            return out.toByteArray();
        }

        return new byte[0];
    }
}
