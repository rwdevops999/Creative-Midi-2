package util.voice;

import java.io.File;

public class MS2SProvider implements InstrumentProvider {
    public MS2SProvider(File insFile) {
    }

    public MS2SProvider(String filename) {
        this(new File(filename));
    }

    @Override
    public VoiceType getSourceType() {
        return VoiceType.MS2S;
    }
}
