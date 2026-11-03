package scenes.eventlist.editor;

import entity.midi.MidiEventInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
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

import javax.sound.midi.Sequence;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class FileHandlerPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(FileHandlerPane.class);

    public FileHandlerPane() {
        super();

        setId("FileHandlerPane");

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        setFillHeight(false);

        setPadding(new Insets(0, 0, 0, 5));
    }

    private EventListEditorPane parent;
    public FileHandlerPane(EventListEditorPane owner) {
        this();

        this.parent = owner;

        setPaneBackgroundWithColor(this, Color.DARKGRAY.darker());

        buildPane();
    }

    private final BooleanProperty saveDisable = new SimpleBooleanProperty(true);
    private File selectedFile = null;
    private final StringProperty fileName = new SimpleStringProperty("<no file>");

    private  void buildPane() {
        logger.debug("Building {}", getId());

        Label staticFileLabel = new Label("MIDI");
        getChildren().add(staticFileLabel);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a MIDI file");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("MIDI", "*.mid")
        );

        Button fileSelectButton = new Button("Upload ...");
        fileSelectButton.setMaxHeight(Button.USE_PREF_SIZE);
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
            handleSaveFile();
        });
        getChildren().add(fileSaveButton);

        logger.debug("Built {}", getId());
    }

    private LinkedList<MidiEventInfo> currentEvents = null;
    private Sequence currentSequence = null;
    public void setEventInfo(Sequence sequence, Queue<MidiEventInfo> events) {
        currentEvents = new LinkedList<>(events);
        currentSequence = sequence;
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
