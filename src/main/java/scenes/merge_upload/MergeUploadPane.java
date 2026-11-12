package scenes.merge_upload;

import entity.NameEntity;
import entity.playlist.Song;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexDetailsPane;
import scenes.sysex.SysexListPane;
import scenes.sysex.consumers.*;
import scenes.sysex.statemachine.StateMachine;
import scenes.sysex.statemachine.States;

import java.util.function.BiConsumer;

public class MergeUploadPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(MergeUploadPane.class);

    public MergeUploadPane() {
        super();

        setId("MergeUploadPane");

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }
}
