package scenes.tests.panes;

import entity.midi.Midi;
import fx.dialog.SrcDstListDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import util.keyboards.yamaha.KbCommunication;
import util.midi.MidiWriter;
import util.sysex.SysexWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static util.Util.setPaneBackground;
import static util.keyboards.yamaha.General.hex;

public class Test2Pane extends HBox {
    private SysexWriter writer = SysexWriter.getInstance();
    private MidiWriter mwriter = MidiWriter.getInstance();
    private KbCommunication kbComm = KbCommunication.getInstance();

    public Test2Pane() {
        super();

        setId("Test2Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
    }

    private byte[] ack = new byte[] { (byte) 0xF0, 0x43, 0x50, 0x00, 0x03, 0x00, (byte) 0xF7 };

    private void runTest() {
        try {
            byte[] fileClose = new byte[] { (byte) 0xF0, 0x43, 0x50, 0x00, 0x06, 0x00, (byte) 0xF7 };
            kbComm.send(fileClose);
            kbComm.waitForResponse(ack, 5000);

            byte[] unlockSession = new byte[] { (byte) 0xF0, 0x43, 0x50, 0x00, 0x00, 0x01, 0x00, 0x00, (byte) 0xF7 };
            kbComm.send(unlockSession);
            kbComm.waitForResponse(ack, 5000);
        } catch (MidiUnavailableException | InvalidMidiDataException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        /*        try {
            ShortMessage sm = new ShortMessage(ShortMessage.STOP);

            mwriter.send(sm);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        } */
    }

}
