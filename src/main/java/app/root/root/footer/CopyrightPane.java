package app.root.root.footer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;

public class CopyrightPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(CopyrightPane.class);

    public CopyrightPane() {
        super();

        setId("CopyrightPane");

        setAlignment(Pos.CENTER_LEFT);
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

        Font font = Font.font("Arial", FontWeight.NORMAL, 12);
        Text copyright = new Text("© 2026-2027 Rudi Welter");
        copyright.setFont(font);
        HBox.setMargin(copyright, new Insets(0, 0, 0, 5));
        getChildren().add(copyright);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS

    public FooterPane getFooterPane() {
        return parent;
    }
}
