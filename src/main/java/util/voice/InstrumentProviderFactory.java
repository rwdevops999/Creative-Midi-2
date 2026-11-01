package util.voice;

import entity.voice.KeyboardVoice;
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

    public static InstrumentProvider getProvider(KeyboardVoice voice) {
        if (VoiceType.INS.equals(voice.getVoiceType())) {
            return new InsProvider(voice.getName() + ".ins");
        } else if (VoiceType.MS2S.equals(voice.getVoiceType())) {
            return new MS2SProvider(voice.getName() + ".txt");
        }

        return null;
    }
}
