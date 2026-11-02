package scenes.eventlist.editor.subpanes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static util.Util.setPaneWidthAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class MidiInfoFilenamePane extends HBox {
    private final StringProperty dsSongName = new SimpleStringProperty("<no song selected>");
    public MidiInfoFilenamePane() {
        super();

        setSpacing(5);
        setPadding(new Insets(0, 0, 0, 5));
    }

    public MidiInfoFilenamePane(Pane owner) {
        this();

        setPaneWidthAsPercentage(this, owner, 48);

        buildPane();
    }

    private void buildPane() {
        setStyle("-fx-border-color: black;");

        Label songnameLabel = new Label();
        songnameLabel.textProperty().bind(dsSongName);
        getChildren().add(songnameLabel);
    }

    public void setSongName (String name) {
        dsSongName.set(name);
    }
}
