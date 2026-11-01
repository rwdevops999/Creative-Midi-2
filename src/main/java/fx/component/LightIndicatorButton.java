package fx.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static util.app.colors.ColorScheme.getColor;

public class LightIndicatorButton extends ToggleButton {

    private static final double LIGHT_WIDTH = 20.0;
    private static final double LIGHT_HEIGHT = 6.0;

    private static final double DEFAULT_BUTTON_WIDTH = 45.0;

    private final Color COLOR_LIGHT_OFF;
    private final Color COLOR_LIGHT_ON;
    private final Color COLOR_BORDER_ON;

    private static final Color COLOR_BORDER_OFF = Color.BLACK;
    private static final Color COLOR_TEXT = Color.LIGHTGRAY;

    private final Rectangle lightIndicator;
    private final Label buttonLabel;
    private final DropShadow glowEffect;

    public LightIndicatorButton(String text, String color) {
        this(text, DEFAULT_BUTTON_WIDTH, color);
    }

    public LightIndicatorButton(String text, double size, String color) {
        COLOR_LIGHT_OFF = getColor("button", "light off", color);
        COLOR_LIGHT_ON = getColor("button", "light on", color);
        COLOR_BORDER_ON = getColor("button", "border on", color);

        // Force the button to take this width always
        this.setMinSize(size, size);
        this.setPrefSize(size, size);
        this.setMaxSize(size, size);

        // make the button black and remove the JavaFX-borders
        this.setStyle("-fx-background-color: #000000; " +
                "-fx-background-radius: 4; " +
                "-fx-border-color: #222222; " +
                "-fx-border-radius: 4; " +
                "-fx-border-width: 1;");

        lightIndicator = new Rectangle(LIGHT_WIDTH, LIGHT_HEIGHT);
        lightIndicator.setArcWidth(2);
        lightIndicator.setArcHeight(2);
        lightIndicator.setStrokeWidth(1.0);

        buttonLabel = new Label(text);
        buttonLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 8));

        // Break up long text with '...' if the button is too small
        buttonLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        glowEffect = new DropShadow();
        glowEffect.setBlurType(BlurType.THREE_PASS_BOX);
        glowEffect.setColor(Color.RED);
        glowEffect.setRadius(10);

        VBox graphicContainer = new VBox(6, lightIndicator, buttonLabel);
        graphicContainer.setAlignment(Pos.CENTER);
        setGraphic(graphicContainer);

        updateVisuals(false);

        this.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateVisuals(isSelected));
    }

    private void updateVisuals(boolean isSelected) {
        buttonLabel.setTextFill(COLOR_TEXT);

        if (isSelected) {
            lightIndicator.setFill(COLOR_LIGHT_ON);
            lightIndicator.setStroke(COLOR_BORDER_ON);
            lightIndicator.setEffect(glowEffect);
        } else {
            lightIndicator.setFill(COLOR_LIGHT_OFF);
            lightIndicator.setStroke(COLOR_BORDER_OFF);
            lightIndicator.setEffect(null);
        }
    }
}
