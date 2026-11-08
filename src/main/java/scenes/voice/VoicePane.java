package scenes.voice;

import entity.NameEntity;
import entity.sysex.Sysex;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexDetailsPane;
import scenes.sysex.SysexListPane;
import scenes.sysex.consumers.*;
import scenes.sysex.statemachine.StateMachine;
import scenes.sysex.statemachine.States;
import util.Util;

import java.util.function.BiConsumer;

import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class VoicePane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoicePane.class);

    public VoicePane() {
        super();

        setId("VoicePane");

        buildPane();
    }

    private VoiceSearchPane voiceSearchPane;
    private VoiceResultPane voiceResultPane;
    private void buildPane() {
        logger.debug("Building {}", getId());

        setPaneBackground(this);

        /*
         * This needs to be created first because it is used in voiceSearchPane
         */
        voiceResultPane = new VoiceResultPane(this, 74.8);
        voiceSearchPane = new VoiceSearchPane(this, 25);
        getChildren().add(voiceSearchPane);
        getChildren().add(voiceResultPane);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return voiceSearchPane;
    }

    public VoiceResultPane getVoiceResultPane() {
        return voiceResultPane;
    }
}
