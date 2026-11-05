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

import static util.app.colors.ColorScheme.getColor;

public class VoicePane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoicePane.class);

    public VoicePane() {
        super();

        setId("VoicePane");

        Util.showSmallPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        VoiceSearchPane voiceSearchPane = new VoiceSearchPane(this, 30);
        getChildren().add(voiceSearchPane);
        VoiceResultPane voiceResultPane = new VoiceResultPane(this, 69.8);
        getChildren().add(voiceResultPane);

        logger.debug("Built {}", getId());
    }
}
