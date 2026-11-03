package util.midi.eventprocessors;

import lombok.Getter;

public class SysexProcessor {
    @Getter
    private static SysexProcessor instance = new SysexProcessor();

    private SysexProcessor() {
    }
}
