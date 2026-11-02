package scenes.eventlist.editor;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;
import util.Util;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class EventListEditorPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(EventListEditorPane.class);

    public EventListEditorPane() {
        super();

        setId("EventListEditorPane");
    }

    private EventListPane parent;
    public EventListEditorPane(EventListPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        getChildren().add(new FileHandlerPane(this, 10));
        getChildren().add(new EditorPane(this, 90));

        logger.debug("Built {}", getId());
    }

     // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }
}
