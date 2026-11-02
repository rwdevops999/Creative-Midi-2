package scenes.eventlist.editor.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static util.Util.setPaneWidthAsPercentage;

public class MidiInfoTimeSignaturePane extends HBox {
    private final StringProperty value = new SimpleStringProperty("4/4");
    public MidiInfoTimeSignaturePane() {
        super();

        setSpacing(5);
        setPadding(new Insets(0, 0, 0, 5));
    }

    public MidiInfoTimeSignaturePane(Pane owner) {
        this();

        setPaneWidthAsPercentage(this, owner, 13);

        buildPane();
    }

    private void buildPane() {
        setStyle("-fx-border-color: black;");

        Label signatureValueLabel = new Label();
        signatureValueLabel.textProperty().bind(value);
        getChildren().add(signatureValueLabel);
    }

    public void setTimeSignature(String signature) {
        value.setValue(signature);
    }
}
