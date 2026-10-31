package fx.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class IconButton extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(IconButton.class);

    public IconButton() {
        super();

        setSpacing(5);
        setAlignment(Pos.CENTER);
    }

    public IconButton(String name, String tooltip, String iconname, EventHandler<ActionEvent> handler) {
        this();

        Tooltip tooltipField = new Tooltip( tooltip);

        Image icon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(iconname)));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        Button button = new Button();
        button.setId(name);
        button.setGraphic(imageView);
        button.setOnAction(handler);
        button.setTooltip(tooltipField);

        getChildren().add(button);
    }
}
