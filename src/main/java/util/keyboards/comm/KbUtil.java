package util.keyboards.comm;

import org.apache.commons.io.FilenameUtils;
import util.keyboards.yamaha.entity.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static util.keyboards.yamaha.General.asHex;

public class KbUtil {
    public static String byteToHex(byte data) {
        return String.format("%02X",data);
    }

    public static void extractName(byte[] packet, DriveEntity entity) {
        if (packet == null || packet.length < 30) {
            return;
        }

        int start = 27;

        StringBuilder filename = new StringBuilder();

        for (int i = start; i < packet.length - 1; i++) {
            int value = packet[i] & 0xFF;

            if (value == 0) {
                continue;
            }

            if (value >= 32 && value <= 126) {
                filename.append((char) value);
            }
        }

        String result = filename.toString().trim();
        if (result.startsWith("%") || result.startsWith("#")) {
            result = result.substring(1);
        }

        if (FilenameUtils.getExtension(result).equals("")) {
            KbFolder folder = new KbFolder(result);
            entity.getFolders().add(folder);
        } else {
            KbFile file = new KbFile(result);
            entity.getFiles().add(file);
        }
    }

    public static byte[] charsToBytes(String text) {
        if (text == null || text.isEmpty()) {
            return new byte[0];
        }

        byte[] rawBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();

        for (int i = 0; i < rawBytes.length; i += 7) {
            int msbFlag = 0;
            int chunkLength = Math.min(7, rawBytes.length - i);
            byte[] chunk = new byte[chunkLength];

            for (int j = 0; j < chunkLength; j++) {
                int b = rawBytes[i + j] & 0xFF;

                // Record bit 7 (0x80) in the flag byte and mask it out of the data byte
                if ((b & 0x80) != 0) {
                    msbFlag |= (1 << (6 - j));
                }

                chunk[j] = (byte) (b & 0x7F);
            }

            encoded.write(msbFlag);
            encoded.write(chunk, 0, chunkLength);
        }

        return encoded.toByteArray();
    }

    /**
     * Decodes Yamaha 7-in-8 SysEx payload bytes back into a readable String.
     *
     * Example: "00 31 3A 5C 56 4F 49 43 00 45 5C 2A 2E 2A" -> "1:\\VOICE\\*.*"
     */
    public static String bytesToChars(byte[] encodedBytes) {
        if (encodedBytes == null || encodedBytes.length == 0) {
            return "";
        }

        ByteArrayOutputStream decoded = new ByteArrayOutputStream();

        for (int i = 0; i < encodedBytes.length; i += 8) {
            int msbFlag = encodedBytes[i] & 0xFF;

            for (int j = 0; j < 7 && (i + 1 + j) < encodedBytes.length; j++) {
                int dataByte = encodedBytes[i + 1 + j] & 0x7F;

                // Restore bit 7 (0x80) if the corresponding flag bit is set
                if ((msbFlag & (1 << (6 - j))) != 0) {
                    dataByte |= 0x80;
                }

                // Stop if a 0x00 null terminator is reached
                if (dataByte == 0x00) {
                    break;
                }

                decoded.write(dataByte);
            }
        }

        return new String(decoded.toByteArray(), StandardCharsets.ISO_8859_1);
    }

    /**
     * Formats a byte array as an uppercase space-separated hex string.
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }

    /**
     *
     * @param path e.g. 1:\\VOICE\\*.*
     *
     * @return
     */
    public static byte[] buildDirRequestSysEx(byte[] sysexStart, String path) {
        byte[] encodedPath = charsToBytes(path);

        // Total payload length = encoded path bytes + 1 trailing 0x00 terminator
        int payloadLength = encodedPath.length + 1;

        // 14-bit MIDI length encoding (7 bits per byte)
        byte lenMsb = (byte) ((payloadLength >> 7) & 0x7F);
        byte lenLsb = (byte) (payloadLength & 0x7F);

        ByteArrayOutputStream packet = new ByteArrayOutputStream();


        for (int i = 0; i < sysexStart.length; i++) {
            packet.write(sysexStart[i]);
        }

        // Dynamic 2-byte length
        packet.write(lenMsb);
        packet.write(lenLsb);

        // 7-bit encoded path + trailing 0x00 + F7 end-of-sysex
        packet.write(encodedPath, 0, encodedPath.length);
        packet.write(0x00);
        packet.write(0xF7);

        return packet.toByteArray();
    }

    public static List<String> getFilesFrom(Keyboard kb, String drive, String folder, KbCommunication kbComm) throws Exception {
        List<String> files = new ArrayList<>();

        KbDrive userDrive = kb.getDrives().stream().filter(d -> drive.equals(d.getName())).findFirst().orElse(null);
        if (userDrive != null) {
            KbFolder songFolder = userDrive.getFolders().stream().filter(f -> folder.equals(f.getName())).findFirst().orElse(null);
            if (songFolder != null) {
                String sysex = SysexBuilder.getFilesFromFolderSysex(songFolder, userDrive.getId());
                kbComm.retrieveData(songFolder, sysex);

                if (! songFolder.getFiles().isEmpty()) {
                    for (KbFile kbFile : songFolder.getFiles()) {
                        files.add(FilenameUtils.removeExtension(kbFile.getName()));
                    }
                }
            }
        }

        return files;
    }

}
