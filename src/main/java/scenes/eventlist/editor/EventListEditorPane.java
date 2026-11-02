package scenes.eventlist.editor;

import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;

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

        RowConstraints rowFileHandler = new RowConstraints();
        rowFileHandler.setPercentHeight(6);
        rowFileHandler.setFillHeight(true);

        RowConstraints rowFileInfo = new RowConstraints();
        rowFileInfo.setPercentHeight(6);
        rowFileInfo.setFillHeight(true);

        RowConstraints rowFileEditor = new RowConstraints();
        rowFileEditor.setPercentHeight(88);
        rowFileEditor.setFillHeight(true);

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

     // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }

    public void processFile (File midiFile) {

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
