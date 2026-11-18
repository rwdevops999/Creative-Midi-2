package scenes.tests.panes;

import entity.midi.Midi;
import fx.dialog.SrcDstListDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import util.midi.MidiWriter;
import util.sysex.SysexWriter;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static util.Util.setPaneBackground;
import static util.keyboards.yamaha.General.hex;

public class Test2Pane extends HBox {
    private SysexWriter writer = SysexWriter.getInstance();
    private MidiWriter mwriter = MidiWriter.getInstance();

    public Test2Pane() {
        super();

        setId("Test2Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
    }

    private void runTest() {
        try {
            writer.sendBytes(hex("F0 43 60 7D F7"));
            Thread.sleep(1000);
            writer.sendBytes(hex("F0 43 73 01 52 25 11 00 02 00 00 F7"));
        } catch (Exception ex) {
            System.out.println("EXCEPTION: " + ex.getMessage());
        }

/*        try {
            ShortMessage sm = new ShortMessage(ShortMessage.STOP);

            mwriter.send(sm);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        } */
    }

    private void testMidi() {

    }
}
