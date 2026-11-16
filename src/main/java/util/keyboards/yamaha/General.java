package util.keyboards.yamaha;

import java.util.Arrays;

/**
 * General utilities
 */
public class General {
    /**
     * Convert a string to hex bytes
     */
    public static byte[] hex(String s) {
        s = s.replaceAll("\\s", "");
        byte[] b = new byte[s.length()/2];
        for (int i=0;i<b.length;i++) b[i]=(byte)Integer.parseInt(s.substring(2*i,2*i+2),16);
        return b;
    }

    /**
     * convert bytes as hex string
     */
    public static String asHex(byte[] data) {
        if (data == null) {
            return "NULL";
        }

        StringBuilder sb =
                new StringBuilder();

        for (byte b : data) {
            sb.append(
                    String.format(
                            "%02X ",
                            b & 0xFF
                    )
            );
        }

        return sb.toString().trim();
    }

    /**
     * check is bytes start with prefix
     */
    public static boolean starts(byte[] b, int... prefix) {
        if (b.length < prefix.length) {
            return false;
        }

        for (int i=0;i<prefix.length;i++) {
            if ((b[i]&255)!=prefix[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * check is bytes array start with prefix bytes array
     */
    public static boolean startsWith(byte[] array, byte[] prefix) {
        if (array == null || prefix == null) {
            return false;
        }
        if (array.length < prefix.length) {
            return false;
        }
        // Compares array[0...prefix.length] with prefix[0...prefix.length]
        return Arrays.equals(array, 0, prefix.length, prefix, 0, prefix.length);
    }
}
