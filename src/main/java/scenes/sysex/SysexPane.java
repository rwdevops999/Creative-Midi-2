package scenes.sysex;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysexPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(SysexPane.class);

    public SysexPane() {
        super();

        setId("SysexPane");

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }
}
