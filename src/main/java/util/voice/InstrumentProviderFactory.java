package util.voice;

import org.apache.commons.io.FilenameUtils;

public class InstrumentProviderFactory {
    public static InstrumentProvider getProvider(String filename) {
        String extension = FilenameUtils.getExtension(filename);

        if (extension.equalsIgnoreCase("ins")) {
            return new InsProvider(filename);
        } else if (extension.equalsIgnoreCase("txt")) {
            return new MS2SProvider(filename);
        }

        return null;
    }
}
