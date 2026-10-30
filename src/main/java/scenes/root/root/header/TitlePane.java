package scenes.root.root.header;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;

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

        buildPane();
    }

    private static final int ICON_SIZE = 64;

    private void buildPane() {
        logger.debug("[CM_TITLE_PANE] Building {}", getId());

        Image icon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icons/appicon.png")));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(ICON_SIZE);
        imageView.setFitHeight(ICON_SIZE);
        imageView.setPreserveRatio(true);
        getChildren().add(imageView);
        HBox.setMargin(imageView, new Insets(10));

        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/GreatVibes-Regular.ttf");
        Font font = Font.loadFont(fontStream, 42);
        Text title = new Text("Creative Midi");
        title.setFont(font);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().add(title);

        logger.debug("[CM_TITLE_PANE] Building {}", getId());
    }

    // ACCESSORS
    public HeaderPane getHeaderPane() {
        return parent;
    }
}
