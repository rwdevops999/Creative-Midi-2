package scenes.eventlist.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.editor.subpanes.MidiInfoDurationPane;
import scenes.eventlist.editor.subpanes.MidiInfoFilenamePane;
import scenes.eventlist.editor.subpanes.MidiInfoTempoPane;
import scenes.eventlist.editor.subpanes.MidiInfoTimeSignaturePane;
import util.app.AppInfo;

import java.io.File;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class FileInfoPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(FileInfoPane.class);

    public FileInfoPane() {
        super();

        setId("FileInfoPane");

        setPadding(new Insets(2));
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);
    }

    private EventListEditorPane parent;
    public FileInfoPane(EventListEditorPane owner) {
        this();

        this.parent = owner;

        setPaneBackground(this);

        showPaneBorder(this, getColor("border", "black"));

        buildPane();
    }

    private  void buildPane() {
        logger.debug("Building {}", getId());

        MidiInfoFilenamePane filenamePane = new MidiInfoFilenamePane(this);
        getChildren().add(filenamePane);

        MidiInfoTempoPane tempoPane = new MidiInfoTempoPane(this);
        getChildren().add(tempoPane);

        MidiInfoTimeSignaturePane tsPane = new MidiInfoTimeSignaturePane(this);
        getChildren().add(tsPane);

        MidiInfoDurationPane durationPane = new MidiInfoDurationPane(this);
        getChildren().add(durationPane);

        logger.debug("Built {}", getId());
    }

    public void setFilename(String name) {
        MidiInfoFilenamePane filenamePane = (MidiInfoFilenamePane) getChildren().filtered(n -> n instanceof MidiInfoFilenamePane).stream().findFirst().orElse(null);
        if (filenamePane != null) {
            filenamePane.setSongName(FilenameUtils.removeExtension(name));
        }
    }

    public void setBPM(int bpm) {
        MidiInfoTempoPane tempoPane = (MidiInfoTempoPane) getChildren().filtered(n -> n instanceof MidiInfoTempoPane).stream().findFirst().orElse(null);
        if (tempoPane != null) {
            tempoPane.setBpm(""+bpm);
        }
    }

    public void setTimeSignature(String timeSignature) {
        MidiInfoTimeSignaturePane signaturePane = (MidiInfoTimeSignaturePane) getChildren().filtered(n -> n instanceof MidiInfoTimeSignaturePane).stream().findFirst().orElse(null);
        if (signaturePane != null) {
            signaturePane.setTimeSignature(timeSignature);
        }
    }

    public void setDuration(String duration) {
        MidiInfoDurationPane durationPane = (MidiInfoDurationPane) getChildren().filtered(n -> n instanceof MidiInfoDurationPane).stream().findFirst().orElse(null);
        if (durationPane != null) {
            durationPane.setDuration(duration);
        }
    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return this.parent;
    }
}
