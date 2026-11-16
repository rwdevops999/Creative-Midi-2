package scenes.tests.panes;

import entity.midi.Midi;
import entity.voice.Group;
import entity.voice.Patch;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import util.keyboards.comm.SysexBuilder;
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
    }

    private void runTest() {
        System.out.println("Drive 0 = " + SysexBuilder.getDrive(0));
        System.out.println("Drive 1 = " + SysexBuilder.getDrive(1));
    }
}
