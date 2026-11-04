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
        int x = 15;

        int y1 = x * 128;
        int y2 = x << 7;

        System.out.println("x: " + x + " y1: " + y1 + " y2: " + y2);
    }
}
