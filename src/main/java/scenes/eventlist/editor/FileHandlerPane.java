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
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;
import util.app.AppInfo;
import util.midi.MidiFileWriter;
import util.properties.AppProperties;

import javax.sound.midi.Sequence;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;
import static util.properties.PropertyKeys.WORK_DIR;

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
            if (AppInfo.INSTANCE.getWorkingDir() != null) {
                fileChooser.setInitialDirectory(new File(AppInfo.INSTANCE.getWorkingDir()));
            }
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

    public void updateSequence(Sequence sequence) {
        currentSequence = sequence;
    }

    public void setFileHasChanged() {
        saveDisable.set(false);
    }

    public void handleSaveFile() {
        if (selectedFile != null) {
            String workPath = AppInfo.INSTANCE.getWorkingDir();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = LocalDateTime.now().format(formatter);
            String newFilename = insertTimestamp(selectedFile.getName(), timestamp);

            String newMidiFilename = workPath + "/" + newFilename;

            File file = new File(newMidiFilename);
            try {
                MidiFileWriter writer = new MidiFileWriter();
                writer.writeMidi(currentSequence, file);
                Notifications.create()
                        .title("File Saved")
                        .text("Saved the midi file to '" +  newMidiFilename + "'")
                        .hideAfter(Duration.seconds(3)) // Automatically hides after 5 seconds
                        .position(Pos.TOP_LEFT)     // Set corner position on screen
                        .showInformation();
                AppInfo.INSTANCE.setMidifileChanged(false);
            } catch (Exception e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }
        }
    }

    public String insertTimestamp(String fileName, String timestamp) {
        String nameWithoutExt = FilenameUtils.removeExtension(fileName);
        String extension = FilenameUtils.getExtension(fileName);

        // Regex matches: underscore followed by 4 digits, 2 digits, 2 digits,
        // and time digits separated by dashes (e.g., _2026-11-05_11-28-00)
        String regex = "_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}";

        // Remove the old timestamp if it exists
        nameWithoutExt = nameWithoutExt.replaceAll(regex, "");

        return nameWithoutExt + "_" + timestamp + "." + extension;
    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return this.parent;
    }
}
