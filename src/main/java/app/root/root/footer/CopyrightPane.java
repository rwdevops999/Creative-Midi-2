package app.root.root.footer;

import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.util.setPaneSizeAsPercentage;
import static util.util.showPaneBorder;

public class CopyrightPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(CopyrightPane.class);

    public CopyrightPane() {
        super();

        setId("CopyrightPane");
    }

    private FooterPane parent;
    public CopyrightPane(FooterPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "green"));

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
