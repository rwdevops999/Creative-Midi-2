package scenes.tests.panes;

import entity.sysex.Sysex;
import javafx.scene.layout.HBox;
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
        MidiTypeFilterPane midiTypeFilterPane = new MidiTypeFilterPane(this);
        getChildren().add(midiTypeFilterPane);
    }

    private void runTest() {
    }
}
