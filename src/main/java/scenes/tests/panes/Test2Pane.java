package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.communication.CommunicationType;
import util.midi.MidiWriter;
import util.registry.CommunicationMessage;

import static util.Util.setPaneBackground;

public class Test2Pane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(Test2Pane.class);

    public Test2Pane() {
        super();

        setId("Test2Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        setSpacing(5);
        buildPane();
    }

    private void buildPane() {
        LightIndicatorButton i1 = new LightIndicatorButton("Inbound", "blue");
        i1.setOnAction(event -> {
            MidiWriter.getInstance().send();
        });

        getChildren().add(i1);
    }

    private void runTest() {

    }
}
