package util.voice;

import entity.voice.Group;
import entity.voice.Patch;
import org.apache.commons.io.FilenameUtils;
import util.properties.AppProperties;
import util.voice.parsers.INSParser;
import util.voice.parsers.MS2SParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static util.properties.PropertyKeys.VOICES_DIR;

public class InsProvider implements InstrumentProvider {

    private INSParser parser;
    private MS2SParser linkedParser;

    public InsProvider(String filename) {
        String voicesDir = AppProperties.getInstance().getPropertyAsString(VOICES_DIR," ././data/voices");
        File file = new File(voicesDir, filename);
        this.parser = new INSParser(this, file);

        String linkedFilename = FilenameUtils.removeExtension(filename);
        File linkedFile = new File(voicesDir, linkedFilename + ".txt");
        this.linkedParser = new MS2SParser(linkedFile);
    }

    @Override
    public VoiceType getSourceType() {
        return VoiceType.INS;
    }

    @Override
    public List<Group> getGroups() {
        return this.parser.getRootGroups();
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>(this.parser.getTypes());
    }

    @Override
    public Patch findLinkedPatch(Patch patch) {
        return linkedParser.findPatch(patch);
    }

    @Override
    public Patch findPatch(int msb, int lsb, int pc) {
        return linkedParser.findPatch(msb, lsb, pc);
    }

}
