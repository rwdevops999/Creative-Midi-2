package app.root.root.footer;

import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;

public class StatusPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(StatusPane.class);

    public StatusPane() {
        super();

        setId("StatusPane");
    }

    private FooterPane parent;
    public StatusPane(FooterPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "blue"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    // ACCESSORS

    public FooterPane getFooterPane() {
        return parent;
    }
}
