package util.files;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BinaryFileWriter implements AutoCloseable {
    private final FileOutputStream fos;
    private final BufferedOutputStream bos;

    // De constructor opent de stream éénmalig
    public BinaryFileWriter(String filePath) throws IOException {
        this.fos = new FileOutputStream(filePath);
        this.bos = new BufferedOutputStream(fos);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        bos.write(bytes);
    }

    public void write32BitInt(int value) throws IOException {
        bos.write(new byte[]{
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        });
    }

    public void writeChunk(String name, int size, byte[] data) throws IOException {
        String result = name.substring(0, Math.min(name.length(), size));
        byte[] chunk = result.getBytes(StandardCharsets.UTF_8);

        writeBytes(chunk);
        write32BitInt(data.length);
        writeBytes(data);
    }

    // AutoCloseable zorgt ervoor dat de try-with-resources de buffer flusht en sluit
    @Override
    public void close() throws IOException {
        if (bos != null) {
            bos.flush(); // Duw de allerlaatste bytes naar de schijf
            bos.close(); // Sluit de stream netjes af
        }
    }
}