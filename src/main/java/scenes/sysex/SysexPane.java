package scenes.sysex;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;

public class SysexPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(SysexPane.class);

    public SysexPane() {
        super();

        setId("SysexPane");

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        setLeft(new SysexListPane(this, 30, 100));
        setCenter(new SysexDetailsPane(this, 50, 100));
        setRight(new SceneActionsPane(this, 20, 100));

        logger.debug("Built {}", getId());
    }
}
