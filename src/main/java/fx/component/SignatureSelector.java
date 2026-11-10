package fx.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import lombok.Getter;

@Getter
public class SignatureSelector extends HBox {
    // beats per measure
    public ObjectProperty<Integer> bpmProperty = new SimpleObjectProperty<>(333);
    public ObjectProperty<Integer> beatProperty = new SimpleObjectProperty<>(333);

    public SignatureSelector() {
        create();
    }

    public void setBeatsPerMeasure(Integer bpm) {
        bpmProperty.set(bpm);
    }

    public void setBeat(Integer beat) {
        beatProperty.set(beat);
    }

    public Node create() {
        this.setMaxHeight(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);

        Spinner<Integer> bpmSpinner = new Spinner<>(1, 16, 4);
        bpmSpinner.getValueFactory().valueProperty().bindBidirectional(bpmProperty);
        bpmSpinner.setEditable(true);
        bpmSpinner.getEditor().textProperty().addListener((observable, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                try {
                    StringConverter<Integer> converter = ((SpinnerValueFactory<Integer>) bpmSpinner.getValueFactory()).getConverter();
                    Integer value = converter.fromString(newText);
                    bpmSpinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                }
            }
        });

        Label separatorLabel = new Label("/");

        Spinner<Integer> beatSpinner = new Spinner<>(1, 16, 4);
        beatSpinner.getValueFactory().valueProperty().bindBidirectional(beatProperty);
        beatSpinner.setEditable(true);
        beatSpinner.getEditor().textProperty().addListener((observable, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                try {
                    StringConverter<Integer> converter = ((SpinnerValueFactory<Integer>) beatSpinner.getValueFactory()).getConverter();
                    Integer value = converter.fromString(newText);
                    beatSpinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                }
            }
        });

        this.getChildren().addAll(bpmSpinner, separatorLabel, beatSpinner);

        return this;
    }
}
