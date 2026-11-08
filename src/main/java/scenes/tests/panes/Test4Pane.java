package scenes.tests.panes;

import entity.midi.Midi;
import entity.voice.Group;
import entity.voice.Patch;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import util.midi.MidiHelper;
import util.midi.MidiWriter;

import java.util.ArrayList;
import java.util.List;

import static util.Util.setPaneBackground;

public class Test4Pane extends HBox {

    private MidiHelper midiHelper = MidiHelper.getInstance();

    public Test4Pane() {
        super();

        setId("Test4Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        Patch patch = new Patch(null, "GM&XG", "Tremolo Strings", "", "0", "0", "44");
        midiHelper.sendAsMidi(0, patch);

        Patch patch2 = new Patch(null, "Live", "Allegro", "", "0", "122", "49");
        midiHelper.sendAsMidi(2, patch2);
    }

    private void runTest() {
        Button on = new Button("On");
        on.setOnAction(event -> {
            try {
                Midi midi = new Midi("Note On", 0, 0x90, 0x3C, 0x7F);
                midiHelper.sendMidi(midi);

                Midi midi2 = new Midi("Note On", 2, 0x90, 0x3C, 0x7F);
                midiHelper.sendMidi(midi2);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
        });
        getChildren().add(on);

        Button off = new Button("Off");
        off.setOnAction(event -> {
            Midi midi;

            try {
                // Volume
                midi = new Midi("volume", 2, 0xB0, 0x07, 0x7F);
                midiHelper.sendMidi(midi);

                // Sustain Pedal
                midi = new Midi("sustain pedal", 2, 0xB0, 0x40, 0x00);
                midiHelper.sendMidi(midi);

                // All Controllers Off
                midi = new Midi("all controllers off", 2, 0xB0, 0x79, 0x00);
                midiHelper.sendMidi(midi);

                // All Notes Off
                midi = new Midi("all notes off", 2, 0xB0, 0x7B, 0x00);
                midiHelper.sendMidi(midi);

                // All Sound Off
                midi = new Midi("all sound off", 2, 0xB0, 0x78, 0x00);
                midiHelper.sendMidi(midi);
            } catch (Exception e) {

            }

        });
        getChildren().add(off);
    }
}
