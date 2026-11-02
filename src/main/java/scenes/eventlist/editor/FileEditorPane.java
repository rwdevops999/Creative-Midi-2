package scenes.eventlist.editor;

import entity.midi.MidiEventInfo;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FileEditorPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FileEditorPane.class);

    public FileEditorPane() {
        super();

        setId("EditorPane");
    }

    private EventListEditorPane parent;
    public FileEditorPane(EventListEditorPane owner) {
        this();

        showSmallPaneBorder(this, getColor("border", "green"));

        this.parent = owner;

        buildPane();
    }

    private final int[] columnSizes = {10,4,24,13,7,10,32};
    private TableView<MidiEventInfo> table;

    private  void buildPane() {
        logger.debug("Building {}", getId());

        setStyle("-fx-font-size: 10px;");

        table = new TableView<>();
        table.setFixedCellSize(20.0);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setMaxHeight(Double.MAX_VALUE);

//        createEventColumns(table);
//        createContextMenu(table);

        getChildren().add(table);

        logger.debug("Built {}", getId());
    }

    private void createEventColumns(TableView<MidiEventInfo> table) {
    }

    private void createContextMenu(TableView<MidiEventInfo> table) {
    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return parent;
    }
}
