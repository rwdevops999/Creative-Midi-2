package util.voice;

import entity.voice.Group;
import entity.voice.Patch;

import java.util.List;

public interface InstrumentProvider {
    VoiceType getSourceType();

    List<Group> getGroups();

    List<String> getTypes();

    default Patch findLinkedPatch(Patch patch) {
        return patch;
    }

    default Patch findPatch(int msb, int lsb, int pc) {
        return null;
    }

}
