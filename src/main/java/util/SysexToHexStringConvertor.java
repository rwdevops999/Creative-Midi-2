package util;

public class SysexToHexStringConvertor {
    /**
     * convert byte array values to String of hex values
     *
     * @param data: the byte array
     *
     * @return the string (trimmed the end spaces)
     */
    public static String convertToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString().stripTrailing();
    }

    /**
     * This is excatly the same as above except it returns a StringBuilder
     * @param data: The data to convert to hex string
     * @return the StringBuilder
     */
    public static StringBuilder convertToHexStringbuilder(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }

        return sb;
    }
}
