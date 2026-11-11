package scenes.voice.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import scenes.voice.VoiceFilterPane;
import scenes.voice.VoiceResultPane;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceInfoPane extends VBox {
    public VoiceInfoPane() {
        super();

        setId("VoiceInfoPane");
    }

    private VoiceFilterPane parent;

    private StringProperty voiceRight1 = new SimpleStringProperty("");
    private StringProperty voiceLeft = new SimpleStringProperty("");
    private StringProperty voiceRight2 = new SimpleStringProperty("");

    public VoiceInfoPane(VoiceFilterPane owner, double width, double height) {
        this();

        this.parent = owner;

        Util.setPaneSizeAsPercentage(this, owner, width, height);

        voiceRight1.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && right1Pane != null) {
                right1Pane.setVoice(newValue);
            }
        });

        voiceLeft.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && leftPane != null) {
                leftPane.setVoice(newValue);
            }
        });

        voiceRight2.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && right2Pane != null) {
                right2Pane.setVoice(newValue);
            }
        });

        buildPane();
    }

    private VoiceInfoLinePane right1Pane;
    private VoiceInfoLinePane leftPane;
    private VoiceInfoLinePane right2Pane;

    private void buildPane() {
        getChildren().clear();

        right1Pane = new VoiceInfoLinePane(this, "Right1 (⌃)");
        leftPane = new VoiceInfoLinePane(this, "Left (⌃⇧)");
        right2Pane = new VoiceInfoLinePane(this, "Right1 (⇧)");

        getChildren().addAll(right1Pane, leftPane, right2Pane);
    }

    public void setRight1Voice(String voice) {
        voiceRight1.set(voice);
    }

    public void setLeftVoice(String voice) {
        voiceLeft.set(voice);
    }

    public void setRight2Voice(String voice) {
        voiceRight2.set(voice);
    }

    // ACCESSORS
    public VoiceFilterPane getVoiceFilterPane() {
        return parent;
    }
}
