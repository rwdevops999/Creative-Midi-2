package util.files;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BinaryFileReader implements AutoCloseable {
    private final FileInputStream fis;
    private final BufferedInputStream bis;

    // De constructor opent de stream éénmalig voor het hele bestand
    public BinaryFileReader(String filePath) throws IOException {
        this.fis = new FileInputStream(filePath);
        this.bis = new BufferedInputStream(fis);
    }

    /**
     * Leest exact 'num' bytes. Garandeert dat de buffer vol is (Java 9+).
     */
    public byte[] readBytes(int num) throws IOException {
        byte[] data = bis.readNBytes(num);

        if (data.length < num) {
            throw new IOException("Onverwacht einde van bestand. Verwachtte "
                    + num + " bytes, maar las er slechts " + data.length);
        }
        return data;
    }

    /**
     * Reconstructeert een 32-bit integer uit 4 opeenvolgende bytes (Big-Endian).
     */
    public int read32BitInt() throws IOException {
        byte[] bytes = readBytes(4);
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8)  |
                ((bytes[3] & 0xFF));
    }

    /**
     * Sluit de streams netjes af aan het einde van de try-with-resources.
     */
    @Override
    public void close() throws IOException {
        if (bis != null) {
            bis.close();
        }
    }
}
