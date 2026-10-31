package app.root.root.header;

import app.root.root.header.panes.AppButtons;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;

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
//        showPaneBorder(this, getColor("border", "orange"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        AppButtons appButtons = new AppButtons(this);
        VBox.setVgrow(appButtons, javafx.scene.layout.Priority.ALWAYS);
        getChildren().add(appButtons);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS

    public HeaderPane getHeaderPane() {
        return parent;
    }

}
