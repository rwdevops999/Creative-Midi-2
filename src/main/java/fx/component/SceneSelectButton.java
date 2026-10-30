package fx.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.app.routing.Router;
import util.app.scene.IScene;

import java.util.Objects;

public class SceneSelectButton extends Button {
    public SceneSelectButton() {
        super();
    }

    public SceneSelectButton(IScene ownerScene, int size, String name, String tooltip, String iconname, IScene forScene) {
        this();

        EventHandler<ActionEvent> handler = e -> {
            Router.getInstance().routeTo(ownerScene, forScene, true);
        };

        Tooltip tooltipField = new Tooltip(tooltip);

        Image icon = new Image(Objects.requireNonNull(SceneSelectButton.class.getResourceAsStream("/icons/" + iconname + ".png")));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);

        setPrefSize(size,size);
        setPadding(new Insets(1));
        setId(name);
        setTooltip(tooltipField);
        setGraphic(imageView);
        setOnAction(handler);
        setStyle("-fx-background-color: yellow; -fx-padding: 1;");
    }
}
