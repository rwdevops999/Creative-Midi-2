package fx.component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChannelIndicator extends HBox {

    public ChannelIndicator() {
        super();

        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
    }

    private final BooleanProperty active = new SimpleBooleanProperty(false);

    private Circle led;
    private Color activeColor;
    private Color inactiveColor = Color.DARKGRAY;

    public ChannelIndicator(String labelText, Color ledOnColor, Color ledOffColor) {
        this();

        this.activeColor = ledOnColor;
        this.inactiveColor = ledOffColor;

        led = new Circle(5); // Elegant LED size
        led.setFill(inactiveColor);

        active.addListener((obs, wasActive, isActive) ->
                Platform.runLater(() -> led.setFill(isActive ? this.activeColor : this.inactiveColor))
        );

        this.getChildren().add(led);

        if (labelText != null) {
            Label label = new Label(labelText);
            label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            this.getChildren().add(label);
        }
    }

    public void setActive(boolean isActive) {
        this.active.set(isActive);
    }

    public BooleanProperty activeProperty() {
        return active;
    }
}
