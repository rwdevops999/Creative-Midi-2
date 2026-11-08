package fx.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ScaleSelector extends HBox {
    public static String[] scales = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static String[] pitches = {"major", "minor"};

    public StringProperty scaleProperty = new SimpleStringProperty();
    public StringProperty pitchProperty = new SimpleStringProperty();

    public ScaleSelector(boolean addScaleLabel) {
        create(addScaleLabel);
    }

    public void setScaleValue(String scale) {
        scaleProperty.set(scale);
        pitchProperty.set("major");
    }

    public void setPitchValue(String pitch) {
        pitchProperty.set(pitch);
    }

    public Node create(boolean addScaleLabel) {
        this.setMaxHeight(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);

        // SCALE selector
        Label scaleLabel = new Label("");
        if (addScaleLabel) {
            scaleLabel = new Label("Scale");
        }

        ComboBox<String> scaleComboBox = new ComboBox<>();
        scaleComboBox.setId("ScaleComboBox");
        scaleComboBox.setPromptText(("scale ..."));
        scaleComboBox.getItems().addAll(scales);
        scaleComboBox.valueProperty().bindBidirectional(scaleProperty);

        // PITCH
        Label pitchLabel = new Label("Pitch");

        ComboBox<String> pitchComboBox = new ComboBox<>();
        pitchComboBox.setId("PitchComboBox");
        pitchComboBox.setPromptText("pitch ...");
        pitchComboBox.getItems().addAll(pitches);
        pitchComboBox.valueProperty().bindBidirectional(pitchProperty);

        if (addScaleLabel) {
            this.getChildren().addAll(scaleLabel, scaleComboBox, pitchLabel, pitchComboBox);
        } else {
            this.getChildren().addAll(scaleComboBox, pitchLabel, pitchComboBox);
        }

        return this;
    }
}
