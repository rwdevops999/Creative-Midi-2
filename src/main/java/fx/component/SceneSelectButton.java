package fx.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import scenes.tests.TestScene;
import scenes.tests.panes.Test1Pane;
import scenes.tests.panes.Test2Pane;
import util.app.routing.Router;
import util.app.scene.IScene;

import java.util.Objects;

public class SceneSelectButton extends Button {
    public SceneSelectButton() {
        super();
    }

    public SceneSelectButton(IScene ownerScene, int size, String name, String tooltip, String iconName, IScene forScene) {
        this();

        EventHandler<ActionEvent> handler = e -> Router.getInstance().routeTo(ownerScene, forScene, true);

        Tooltip tooltipField = new Tooltip(tooltip);

        Image icon = new Image(Objects.requireNonNull(SceneSelectButton.class.getResourceAsStream("/icons/" + iconName + ".png")));
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

    public SceneSelectButton(IScene ownerScene, int size, String name, String tooltip, String iconName, String paneName) {
        this();

        EventHandler<ActionEvent> handler = e -> {
            Pane resultingPane = switch (paneName) {
                case "Test1" -> new Test1Pane();
                case "Test2" -> new Test2Pane();
                default -> null;
/*                case "Test3":
                    resultingPane = new Test3Pane();
                    break;
                case "Test4":
                    resultingPane = new Test4Pane();
                    break; */
            };

            Router.getInstance().routeTo(ownerScene, new TestScene(), resultingPane, true);
        };

        Tooltip tooltipField = new Tooltip( tooltip);

        String nameOfIcon = "/icons/" + iconName + ".png";
        Image icon = new Image(Objects.requireNonNull(SceneSelectButton.class.getResourceAsStream(nameOfIcon)));
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
