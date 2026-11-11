package scenes.voice.subpanes;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import static util.app.colors.ColorScheme.getColor;

public class VoiceInfoLinePane extends HBox {
    public VoiceInfoLinePane() {
        super();

        setId("VoiceInfoLinePane");
    }

    private VoiceInfoPane parent;

    public VoiceInfoLinePane(VoiceInfoPane owner, String forVoice) {
        this();

        this.parent = owner;

        buildPane(forVoice);
    }

    private final StringProperty voiceProperty = new SimpleStringProperty();

    private void setLabelBlinking(Label label) {
        Duration blinkSpeed = Duration.seconds(0.5);
        FadeTransition fadeTransition = new FadeTransition(blinkSpeed, label);

        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setAutoReverse(true); // Smoothly fades back in

        // 2. Calculate cycles for a specific total duration (e.g., 5 seconds)
        double totalBlinkDurationInSeconds = 5.0;

        // One complete blink (out and back in) takes 2 * blinkSpeed
        int requiredCycles = (int) (totalBlinkDurationInSeconds / (blinkSpeed.toSeconds() * 2));

        // Multiply by 2 because setCycleCount tracks individual fades (out = 1, in = 1)
        fadeTransition.setCycleCount(requiredCycles * 2);

        // 3. Ensure the text remains fully visible when the time runs out
        fadeTransition.setOnFinished(event -> label.setOpacity(1.0));

        // Start the effect
        fadeTransition.play();
    }

    private void buildPane(String forVoice) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label label = new Label(forVoice + " : ");
        label.setStyle("-fx-font-weight: bold;");

        Label voiceLabel = new Label();
        voiceLabel.setTextFill(getColor("base", "royalblue"));
        voiceLabel.textProperty().bindBidirectional(voiceProperty);

        voiceProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setLabelBlinking(voiceLabel);
            }
        });

        getChildren().addAll(label, spacer, voiceLabel);
    }

    public void setVoice(String voice) {
        voiceProperty.set(voice);
    }

    // ACCESSORS

    public VoiceInfoPane getVoiceInfoPane() {
        return parent;
    }
}
