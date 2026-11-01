package scenes.tests.panes;

import entity.midi.Midi;
import fx.component.LightIndicatorButton;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.communication.CommunicationType;
import util.midi.MidiWriter;
import util.registry.CommunicationMessage;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

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
        setPadding(new Insets(5));
        setSpacing(5);
        buildPane();
    }

    LightIndicatorButton i1;
    private void buildPane() {
        i1 = new LightIndicatorButton("Inbound", "blue");
        getChildren().add(i1);
    }

    private void runTest() {
        i1.setOnAction(event -> {
            testMidi();
        });
    }

    private void testMidi() {
    }
}
