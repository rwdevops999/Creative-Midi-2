package scenes.root.root.header;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.util.*;

public class TitlePane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(TitlePane.class);

    private HeaderPane parent;
    public TitlePane(HeaderPane owner, int width, int height) {
        super();

        this.parent = owner;

        setId("TitlePane");

        setSpacing(25);

        setPaneSizeAsPercentage(this, owner, width, height);

        showPaneBorder(this, getColor("border", "green"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("[CM_TITLE_PANE] Building {}", getId());
        Label xxx = new Label();
        getChildren().add(xxx);

        logger.debug("[CM_TITLE_PANE] Building {}", getId());
    }

    // ACCESSORS
    public HeaderPane getHeaderPane() {
        return parent;
    }
}
