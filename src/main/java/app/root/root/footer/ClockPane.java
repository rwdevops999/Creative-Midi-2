package app.root.root.footer;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.ClockBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneSizeAsPercentage;

public class ClockPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(ClockPane.class);

    public ClockPane() {
        super();

        setId("ClockPane");

        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 0, 2, 0));
    }

    private FooterPane parent;

    public ClockPane(FooterPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        Clock clock = ClockBuilder.create()
                .skinType(Clock.ClockSkinType.INDUSTRIAL)
                .dateVisible(false)
                .maxHeight(parent.getMaxHeight())
                .prefHeight(parent.getPrefHeight())
                .prefWidth(parent.getPrefWidth())
                .running(true)
                .build();
        getChildren().add(clock);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public FooterPane getFooterPane() {
        return parent;
    }
}
