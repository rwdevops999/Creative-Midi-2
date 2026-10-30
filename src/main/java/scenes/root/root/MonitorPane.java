package scenes.root.root;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.util.*;

public class MonitorPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(MonitorPane.class);

    public MonitorPane() {
        super();

        setId("MonitorPane");
    }

    private RootPane parent;
    public MonitorPane(RootPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, parent, width, height);
        setPaneBackground(this);

        showPaneBorder(this, getColor("border", "green"));

        buildPane();
    }

    private void buildPane() {
        logger.info("building Pane {}", getId());

        logger.info("built Pane {}", getId());
    }
/*
    private ListView<ColoredItem> messagesView;
    private final ObservableList<ColoredItem> messages = FXCollections.observableArrayList();

    private void setupView() {
        ListView messagesView = new ListView<>();
        messagesView.setCellFactory(lv -> new ColoredListCell());
        messagesView.setItems(messages);
        getChildren().add(messagesView);

    }
*/
    // ACCESSORS
    public RootPane getRootPane() {
        return parent;
    }
}
