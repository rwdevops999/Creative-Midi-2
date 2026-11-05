package scenes.tests.panes;

import entity.sysex.Sysex;
import javafx.scene.layout.HBox;
import scenes.eventlist.filter.subpanes.ChannelControlPane;
import scenes.eventlist.filter.subpanes.ChannelFilterPane;
import scenes.eventlist.filter.subpanes.MidiTypeFilterPane;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.parametrized.Parameter;
import scenes.sysex.parametrized.ParametrizedFunctionFactory;

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
        int keyNo = 60;
        System.out.println("NOTE = " + calcNote(keyNo).getName());

        keyNo = 118;
        System.out.println("NOTE = " + calcNote(keyNo).getName());

        keyNo = 76;
        System.out.println("NOTE = " + calcNote(keyNo).getName());
    }
}
