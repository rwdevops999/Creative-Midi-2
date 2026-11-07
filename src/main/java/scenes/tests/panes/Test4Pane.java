package scenes.tests.panes;

import entity.sysex.Sysex;
import fx.component.CopyButtonsPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.HBox;
import scenes.eventlist.filter.subpanes.ChannelControlPane;
import scenes.eventlist.filter.subpanes.ChannelFilterPane;
import scenes.eventlist.filter.subpanes.MidiTypeFilterPane;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.parametrized.Parameter;
import scenes.sysex.parametrized.ParametrizedFunctionFactory;
import util.sysex.SysexWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static util.Util.calcNote;
import static util.Util.setPaneBackground;

public class Test4Pane extends HBox {
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
/*        Sysex sysex = new Sysex("test");
        sysex.getContent().setSysex("F0 43 73 01 52 25 11 00 02 00 02 F7");
        try {
            SysexWriter.getInstance().send(sysex);
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            System.out.println("E");
        }
*/
        Sysex sysex2 = new Sysex("test");
        sysex2.getContent().setSysex("F0 43 73 01 52 25 11 01 01 00 F7");
        try {
            SysexWriter.getInstance().send(sysex2);
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            System.out.println("E");
        }
    }
}
