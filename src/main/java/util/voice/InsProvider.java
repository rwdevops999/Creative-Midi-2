package util.voice;

import entity.voice.Group;

import java.io.File;
import java.util.List;

public class InsProvider implements InstrumentProvider {
    public InsProvider(File insFile) {
    }

    public InsProvider(String filename) {
        this(new File(filename));
    }

    @Override
    public VoiceType getSourceType() {
        return VoiceType.INS;
    }

    @Override
    public List<Group> getGroups() {
        return List.of();
    }

    @Override
    public List<String> getTypes() {
        return List.of();
    }
}
