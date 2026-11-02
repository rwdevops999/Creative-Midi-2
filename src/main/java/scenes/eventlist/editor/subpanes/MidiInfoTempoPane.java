package scenes.eventlist.editor.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static util.Util.setPaneWidthAsPercentage;

public class MidiInfoTempoPane extends HBox {
    private final StringProperty value = new SimpleStringProperty("0 bpm");
    public MidiInfoTempoPane() {
        super();

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
        setPadding(new Insets(0, 0, 0, 5));
    }

    public MidiInfoTempoPane(Pane owner) {
        this();

        setPaneWidthAsPercentage(this, owner, 12);

        buildPane();
    }

    private void buildPane() {
        setStyle("-fx-border-color: black;");

        Label tempovalueLabel = new Label();
        tempovalueLabel.textProperty().bind(value);
        getChildren().add(tempovalueLabel);
    }

    public void setBpm(String tempo) {
        value.set(tempo + " bpm");
    }
}
