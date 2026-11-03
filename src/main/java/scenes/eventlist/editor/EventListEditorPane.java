package scenes.eventlist.editor;

import entity.midi.MidiSequence;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;
import util.midi.*;
import util.registry.Registry;

import javax.sound.midi.Sequence;
import java.io.File;

import static util.app.colors.ColorScheme.getColor;

public class EventListEditorPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(EventListEditorPane.class);

    public EventListEditorPane() {
        super();

        setId("EventListEditorPane");
    }

    private EventListPane parent;
    public EventListEditorPane(EventListPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private FileHandlerPane fileHandlerPane;
    private FileInfoPane fileInfoPane;
    private FileEditorPane fileEditorPane;

    private void buildPane() {
        logger.debug("Building {}", getId());

        getChildren().clear();
        getRowConstraints().clear();
        getColumnConstraints().clear();

        fileHandlerPane = new FileHandlerPane(this);
        fileInfoPane = new FileInfoPane(this);
        fileEditorPane = new FileEditorPane(this);

        // Rij 0 (FileHandler): Neem de natuurlijke hoogte van de knoppen aan (COMPUTE_SIZE)
        RowConstraints rowFileHandler = new RowConstraints();
        rowFileHandler.setVgrow(Priority.NEVER);

// Rij 1 (FileInfo): Neem ook de natuurlijke hoogte aan
        RowConstraints rowFileInfo = new RowConstraints();
        rowFileInfo.setVgrow(Priority.NEVER);

// Rij 2 (FileEditor/Tabel): Eis álle resterende verticale ruimte op!
        RowConstraints rowFileEditor = new RowConstraints();
        rowFileEditor.setVgrow(Priority.ALWAYS);

        this.getRowConstraints().addAll(rowFileHandler, rowFileInfo, rowFileEditor);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);
        this.getColumnConstraints().add(col);

        // 5. Voeg de panes toe aan DEZE GridPane (node, columnIndex, rowIndex)
        this.add(fileHandlerPane, 0, 0);
        this.add(fileInfoPane, 0, 1);
        this.add(fileEditorPane, 0, 2);

        // 6. Optioneel: Dwing de subpanes om vertikaal mee te groeien binnen hun cel
        GridPane.setVgrow(fileHandlerPane, Priority.ALWAYS);
        GridPane.setVgrow(fileInfoPane, Priority.ALWAYS);
        GridPane.setVgrow(fileEditorPane, Priority.ALWAYS);

        logger.debug("Built {}", getId());
    }

    public void processFile (File midiFile) {
        getFileInfoPane().setFilename(midiFile.getName());
        Registry.getInstance().showStatus("Loaded midi file " + midiFile.getName());

        // TODO HERE WE ARE GOING TO PROCESSS THE FILE
        /**
         * parse the events from the file and put them in the custom sequence
         */
        MidifileParser midiParser = new MidifileParser(midiFile);
        /**
         * retrieve the custom sequence
         */
        MidiSequence customSequence = midiParser.getCustomSequence();

        try {
            // Convert our custom sequence to javax midi sequence
            Sequence sequence = MidiSequenceConverter.convertToStandardSequence(customSequence);

            // now we have an official midi sequence so we will use it
            MBTCalculator.setPPQ(sequence.getResolution());
            MidiFileHelper.setPPQ(sequence.getResolution());
            MidiFileHelper.retrieveBpmAndTimeSignature(sequence);

            getFileInfoPane().setBPM(MidiFileHelper.getBPM());
            getFileInfoPane().setTimeSignature(MidiFileHelper.getTimeSignature().toString());

            MBTCalculator.setTimeSignature(MidiFileHelper.getTimeSignature());

            getFileInfoPane().setDuration(MidiFileHelper.getDuration());

            MidiFileProcessor.processMidi(sequence);

            getEventListPane().getEventListEditorPane().getFileEditorPane().setEventInfo(sequence, MidiFileProcessor.getEvents());
        } catch (Exception e) {
            logger.debug("Exception. CAUSE {}", e.getMessage());
        }
    }

    // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }

    public FileHandlerPane getFileHandlerPane() {
        return fileHandlerPane;
    }

    public FileInfoPane getFileInfoPane() {
        return fileInfoPane;
    }

    public FileEditorPane getFileEditorPane() {
        return fileEditorPane;
    }
}
