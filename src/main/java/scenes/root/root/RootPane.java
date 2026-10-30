package scenes.root.root;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Constants.*;

public class RootPane extends VBox {
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

        getChildren().add(new MainPane(this, 100, 83));
        getChildren().add(new MonitorPane(this, 100, 17));

        logger.info("built Pane {}", getId());
    }

    // ACCESSORS
    public MainPane getMainPane() {
        return (MainPane)lookup("#MainPage");
    }

    public MonitorPane getMonitorPane() {
        return (MonitorPane)lookup("#MonitorPane");
    }
}
