package util.midi;

import lombok.Getter;

public class MidiWriter {
    @Getter
    private static MidiWriter instance = new MidiWriter();

    private MidiWriter() {
    }

    public void send() {
        System.out.println("Midi Writer: sending");
    }
}
