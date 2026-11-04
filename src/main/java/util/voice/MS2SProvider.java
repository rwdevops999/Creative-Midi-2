package util.voice;

import entity.voice.Group;
import util.properties.AppProperties;
import util.voice.parsers.MS2SParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static util.properties.PropertyKeys.VOICES_DIR;

public class MS2SProvider implements InstrumentProvider {
    private final MS2SParser parser;

    public MS2SProvider(String filename) {
        String voicesDir = AppProperties.getInstance().getPropertyAsString(VOICES_DIR," ././data/voices");
        File file = new File(voicesDir, filename);
        this.parser = new MS2SParser(file);
    }

    @Override
    public VoiceType getSourceType() {
        return VoiceType.MS2S;
    }

    @Override
    public List<Group> getGroups() {
        return parser.getRootGroups();
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>(parser.getTypes());
    }
}
