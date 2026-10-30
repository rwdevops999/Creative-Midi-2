package app.root.root.footer;

import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.root.root.MainPane;

import static util.app.colors.ColorScheme.getColor;
import static util.util.*;

public class FooterPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(FooterPane.class);

    public FooterPane() {
        super();

        setId("FooterPane");
    }

    private MainPane parent;

    public FooterPane(MainPane owner, int width, int height) {
        this();

        this.parent = owner;

        showPaneBorder(this, getColor("border", "red"));
        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        setPaneBackground(this);

        getChildren().add(new CopyrightPane(this, 50, 100));
        getChildren().add(new StatusPane(this, 40, 100));
        getChildren().add(new ClockPane(this, 10, 100));

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public MainPane getMainPane() {
        return parent;
    }
}
