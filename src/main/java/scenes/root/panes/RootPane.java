package scenes.root.panes;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Constants.*;

public class RootPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(RootPane.class);

    public RootPane() {
        super();

        setId("RootPane");

        setPrefSize(APP_WIDTH, APP_HEIGHT+MONITOR_HEIGHT);
        setMinSize(APP_WIDTH, APP_HEIGHT+MONITOR_HEIGHT);
        setMaxSize(APP_WIDTH, APP_HEIGHT+MONITOR_HEIGHT);

        buildPane();
    }

    private void buildPane() {
        logger.info("building Pane {}", getId());

        logger.info("built Pane {}", getId());
    }
}
