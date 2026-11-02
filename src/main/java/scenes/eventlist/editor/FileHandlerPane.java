package scenes.eventlist.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;
import util.app.AppInfo;

import java.io.File;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class FileHandlerPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(FileHandlerPane.class);

    public FileHandlerPane() {
        super();

        setId("FileHandlerPane");

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
    }

    private EventListEditorPane parent;
    public FileHandlerPane(EventListEditorPane owner) {
        this();

        this.parent = owner;

        setPaneBackgroundWithColor(this, Color.DARKGRAY.darker());

        showSmallPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private final BooleanProperty saveDisable = new SimpleBooleanProperty(true);
    private File selectedFile = null;
    private final StringProperty fileName = new SimpleStringProperty("<no file>");

    private  void buildPane() {
        logger.debug("Building {}", getId());

/*        Label staticFileLabel = new Label("File");
        getChildren().add(staticFileLabel);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a MIDI file");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("MIDI", "*.mid")
        );

        Button fileSelectButton = new Button("...");
        fileSelectButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(AppInfo.INSTANCE.getPrimaryStage());

            if (selectedFile != null) {
                fileName.set(FilenameUtils.removeExtension(selectedFile.getName()));

                getEventListEditorPane().processFile(selectedFile);
            }
        });
        getChildren().add(fileSelectButton);

        Button fileSaveButton = new Button("Save");
        fileSaveButton.disableProperty().bind(saveDisable);
        fileSaveButton.setOnAction(e -> {
// TODO            handleSaveFile();
        });
        getChildren().add(fileSaveButton);

        Label selectedFileLabel = new Label();
        selectedFileLabel.textProperty().bind(fileName);
        getChildren().add(selectedFileLabel);
*/
        logger.debug("Built {}", getId());
    }

    public void setFileHasChanged() {
        saveDisable.set(false);
    }

    public void handleSaveFile() {

    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return this.parent;
    }
}
