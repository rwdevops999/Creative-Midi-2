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

    private BooleanProperty dsDisabledAdd = new SimpleBooleanProperty(true);
    private BooleanProperty dsDisabledRemove = new SimpleBooleanProperty(false);

    private void runTest() {
        CopyButtonsPane cbp = new CopyButtonsPane(this);
        getChildren().add(cbp);

        cbp.getAddAllButton().disableProperty().bindBidirectional(dsDisabledAdd);
        cbp.getAddSelectionButton().disableProperty().bindBidirectional(dsDisabledAdd);
        cbp.getDeleteSelectionButton().disableProperty().bindBidirectional(dsDisabledRemove);
        cbp.getDeleteAllButton().disableProperty().bindBidirectional(dsDisabledRemove);

        cbp.getDeleteSelectionButton().setOnAction(event -> {
            System.out.println("DELETE SELECTION PUSHED");
        });

        cbp.getDeleteAllButton().setOnAction(event -> {
            System.out.println("DELETE ALL PUSHED");
        });
    }
}
