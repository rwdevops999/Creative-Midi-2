package fx.component;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ImageButton extends Button {
    public ImageButton() {
        super();
    }

    public ImageButton(String iconName, int width, int height) {
        this();

        Image icon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(iconName)));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        setMaxSize(width, height);
        setPrefSize(width, height);

        setGraphic(imageView);
    }
}
