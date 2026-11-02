package scenes.eventlist.editor.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static util.Util.setPaneWidthAsPercentage;

public class MidiInfoDurationPane extends HBox {
    private final StringProperty value = new SimpleStringProperty("00:00:00");

    public MidiInfoDurationPane() {
        super();

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
        setPadding(new Insets(0, 0, 0, 5));
    }

    public MidiInfoDurationPane(Pane owner) {
        this();

        setPaneWidthAsPercentage(this, owner, 23);

        buildPane();
    }

    private void buildPane() {
        setStyle("-fx-border-color: black;");

        Label durationValueLabel = new Label();
        durationValueLabel.textProperty().bind(value);
        getChildren().add(durationValueLabel);
    }

    public void setDuration(String duration) {
        value.setValue(duration);
    }
}
