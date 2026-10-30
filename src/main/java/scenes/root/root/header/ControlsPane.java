package scenes.root.root.header;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.util.setPaneSizeAsPercentage;
import static util.util.showPaneBorder;

public class ControlsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(ControlsPane.class);

    public ControlsPane() {
        super();

        setId("ControlsPane");
    }

    private HeaderPane parent;
    public ControlsPane(HeaderPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "orange"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    // ACCESSORS

    public HeaderPane getHeaderPane() {
        return parent;
    }

}
