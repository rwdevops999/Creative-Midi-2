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

public class LightIndicatorButton extends ToggleButton {

    private static final double LIGHT_WIDTH = 20.0;
    private static final double LIGHT_HEIGHT = 6.0;

    private static final Color COLOR_LIGHT_OFF = Color.DARKRED;
    private static final Color COLOR_LIGHT_ON = Color.RED;
    private static final Color COLOR_BORDER_OFF = Color.BLACK;
    private static final Color COLOR_BORDER_ON = Color.LIGHTSALMON;
    private static final Color COLOR_TEXT_OFF = Color.GRAY;
    private static final Color COLOR_TEXT_ON = Color.BLACK;

    private final Rectangle lightIndicator;
    private final Label buttonLabel;
    private final DropShadow glowEffect;

    public LightIndicatorButton(String text) {
        super();

        lightIndicator = new Rectangle(LIGHT_WIDTH,LIGHT_HEIGHT);
        lightIndicator.setArcWidth(2);
        lightIndicator.setArcHeight(2);
        lightIndicator.setStrokeWidth(1.0);

        buttonLabel = new Label(text);
        buttonLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

        glowEffect = new DropShadow();
        glowEffect.setBlurType(BlurType.THREE_PASS_BOX);
        glowEffect.setColor(Color.RED);
        glowEffect.setRadius(6);

        VBox graphicContainer = new VBox(6, lightIndicator, buttonLabel);
        graphicContainer.setAlignment(Pos.CENTER);
        setGraphic(graphicContainer);

        updateVisuals(false);

        this.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            updateVisuals(isSelected);
        });
    }

    private void updateVisuals(boolean isSelected) {
        if (isSelected) {
            lightIndicator.setFill(COLOR_LIGHT_ON);
            lightIndicator.setStroke(COLOR_BORDER_ON);
            lightIndicator.setEffect(glowEffect);

            buttonLabel.setTextFill(COLOR_TEXT_ON);
        } else {
            lightIndicator.setFill(COLOR_LIGHT_OFF);
            lightIndicator.setStroke(COLOR_BORDER_OFF);
            lightIndicator.setEffect(null);

            buttonLabel.setTextFill(COLOR_TEXT_OFF);
        }

    }
}
