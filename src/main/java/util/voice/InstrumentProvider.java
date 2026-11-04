package util.voice;

import entity.voice.Group;

import java.util.List;

public interface InstrumentProvider {
    VoiceType getSourceType();

    List<Group> getGroups();

    List<String> getTypes();
}
