package scenes.eventlist.editor;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FileHandlerPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(FileHandlerPane.class);

    public FileHandlerPane() {
        super();

        setId("FileHandlerPane");
    }

    private EventListEditorPane parent;
    public FileHandlerPane(EventListEditorPane owner, int height) {
        this();

        this.parent = owner;

        setPaneHeightAsPercentage(this, owner, height);

        showSmallPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private  void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }
}
