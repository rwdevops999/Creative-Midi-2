package scenes.voice.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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

    private void buildPane(String forVoice) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label label = new Label(forVoice + " : ");
        label.setStyle("-fx-font-weight: bold;");

        Label voiceLabel = new Label();
        voiceLabel.setTextFill(getColor("base", "royalblue"));
        voiceLabel.textProperty().bindBidirectional(voiceProperty);

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
