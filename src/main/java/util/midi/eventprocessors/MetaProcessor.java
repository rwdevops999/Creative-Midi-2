package util.midi.eventprocessors;

import lombok.Getter;

public class MetaProcessor {
    @Getter
    private static MetaProcessor instance = new MetaProcessor();

    private MetaProcessor() {
    }
}
