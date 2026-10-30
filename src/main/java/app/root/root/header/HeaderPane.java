package app.root.root.header;

import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.root.root.MainPane;

import static util.app.colors.ColorScheme.getColor;
import static util.util.*;

public class HeaderPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(HeaderPane.class);

    public HeaderPane() {
        super();

        setId("HeaderPane");

        setVgap(10);
    }

    private MainPane parent;

    public HeaderPane(MainPane owner, int width, int height) {
        this();

        parent = owner;

        showPaneBorder(this, getColor("border", "red"));

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        setPaneBackground(this);

        add(new TitlePane(this, 40, 100), 0, 0, 10, 1);
        add(new SelectorsPane(this, 55, 100), 10, 0, 10, 1);
        add(new ControlsPane(this, 5, 100), 20, 0, 2, 1);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public MainPane getMainPane() {
        return parent;
    }
}
