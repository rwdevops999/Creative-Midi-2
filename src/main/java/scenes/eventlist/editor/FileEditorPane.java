package scenes.eventlist.editor;

import entity.midi.MidiEventInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.colors.ColorScheme;

import javax.sound.midi.Sequence;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

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

        createEventColumns(table);
        createContextMenu(table);

        getChildren().add(table);

        logger.debug("Built {}", getId());
    }

    private void setColumnSize(TableColumn column, int size) {
        double dsize = size / 100.0;
        column.prefWidthProperty().bind(table.widthProperty().multiply(dsize));
        column.minWidthProperty().bind(table.widthProperty().multiply(dsize));
    }

    private void createEventColumns(TableView<MidiEventInfo> table) {
        TableColumn<MidiEventInfo, String> mbtColumn = new TableColumn<>("MBT");
        setColumnSize(mbtColumn, columnSizes[0]);
        mbtColumn.setCellValueFactory(new PropertyValueFactory<>("mbtPosition"));
        mbtColumn.setCellFactory(column -> createStringTableCell());
        table.getColumns().add(mbtColumn);

        TableColumn<MidiEventInfo, Integer> channelColumn = new TableColumn<>("Ch");
        setColumnSize(channelColumn, columnSizes[1]);
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        channelColumn.setCellFactory(column -> createIntegerTableCell());
        table.getColumns().add(channelColumn);

        TableColumn<MidiEventInfo, String> kindColumn = new TableColumn<>("Description");
        setColumnSize(kindColumn, columnSizes[2]);
        kindColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        kindColumn.setCellFactory(column -> createStringTableCell());
        table.getColumns().add(kindColumn);

        TableColumn<MidiEventInfo, String> data2Column = new TableColumn<>("Comment");
        setColumnSize(data2Column, columnSizes[3]);
        data2Column.setCellValueFactory(new PropertyValueFactory<>("comment"));
        data2Column.setCellFactory(column -> createStringTableCell());
        table.getColumns().add(data2Column);

        TableColumn<MidiEventInfo, Integer> dataColumn = new TableColumn<>("Value");
        setColumnSize(dataColumn, columnSizes[4]);
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        dataColumn.setCellFactory(column -> createIntegerTableCell());
        table.getColumns().add(dataColumn);

        TableColumn<MidiEventInfo, String> durationColumn = new TableColumn<>("Duration");
        setColumnSize(durationColumn, columnSizes[5]);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.setCellFactory(column -> createStringTableCell());
        table.getColumns().add(durationColumn);

        TableColumn<MidiEventInfo, String> messageColumn = new TableColumn<>("Message");
        setColumnSize(messageColumn, columnSizes[6]);
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setCellFactory(column -> createStringTableCell());
        table.getColumns().add(messageColumn);
    }

    /**
     * Column display cells
     */
    private TableCell<MidiEventInfo, String> createStringTableCell() {
        return new TableCell<>() {
            // Create a single label reuse instance per cell container
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    // Clear both text and graphic to prevent color bleed artifacts
                    setStyle("-fx-text-fill: black;");
                    setText(null);
                } else {
                    MidiEventInfo eventInfo = getTableRow() != null ? getTableRow().getItem() : null;

                    if (eventInfo != null) {
                        setText(item);
                        String hexColor = ColorScheme.toWebColor(eventInfo.getColor());
                        setStyle("-fx-text-fill: " + hexColor + ";");
                    } else {
                        setText(null);
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        };
    }

    private TableCell<MidiEventInfo, Integer> createIntegerTableCell() {
        return new TableCell<MidiEventInfo, Integer>() {
            // Create a single label reuse instance per cell container
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    // Clear both text and graphic to prevent color bleed artifacts
                    setStyle("-fx-text-fill: black;");
                    setText(null);
                } else {
                    MidiEventInfo eventInfo = getTableRow() != null ? getTableRow().getItem() : null;

                    if (eventInfo != null) {
                        setText(item.toString());
                        String hexColor = ColorScheme.toWebColor(eventInfo.getColor());
                        setStyle("-fx-text-fill: " + hexColor + ";");
                    } else {
                        setText(null);
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        };
    }

    private final BooleanProperty contextMenuItemDisable = new SimpleBooleanProperty(true);
    private void createContextMenu(TableView<MidiEventInfo> table) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteEvent = new MenuItem("Delete");
        deleteEvent.disableProperty().bindBidirectional(contextMenuItemDisable);
        deleteEvent.setOnAction(e -> {
/*            List<MidiEventInfo> selectedEvents = table.getSelectionModel().getSelectedItems();

            for (MidiEventInfo selectedEvent : selectedEvents) {
                deleteEvent(selectedEvent, currentSequence);
            }

            getEventsPane().getEventlistPane().getFileSelectionPane().updateSequence(currentSequence);
            executeFiltering();

            ApplicationInfo.getInstance().setMidiChanged(true);

            setFileHasChangedCallback(); */
        });
        contextMenu.getItems().add(deleteEvent);

        SeparatorMenuItem sep = new SeparatorMenuItem();
        contextMenu.getItems().add(sep);

        MenuItem infoEvent = new MenuItem("Info");
        infoEvent.disableProperty().bindBidirectional(contextMenuItemDisable);
        infoEvent.setOnAction(e -> {
/*            List<MidiEventInfo> selectedEvents = table.getSelectionModel().getSelectedItems();

            EventDetailDialog dialog = new EventDetailDialog(selectedEvents.get(0));
            dialog.showAndWait(); */
        });
        contextMenu.getItems().add(infoEvent);

        table.setContextMenu(contextMenu);
    }

    private List<MidiEventInfo> currentEvents = new ArrayList<>();
    private Sequence currentSequence = null;
    public void setEventInfo(Sequence sequence, Queue<MidiEventInfo> events) {
        currentEvents = new ArrayList<>(events);
        currentSequence = sequence;

        // Pass this information also to the File Handling Pane (just in case for saving)

        getEventListEditorPane().getFileHandlerPane().setEventInfo(sequence, events);

        if (table != null) {
            executeFiltering();
        }
    }

    private void executeFiltering() {
        List<MidiEventInfo> filteredEvents = new ArrayList<>(currentEvents);

        if (! filteredEvents.isEmpty()) {
            // TODO handle Filtering here

            List<MidiEventInfo> sortedQueue = filteredEvents.stream()
                    .sorted(Comparator.comparing(MidiEventInfo::getMbtPosition)).toList();

            contextMenuItemDisable.set(filteredEvents.isEmpty());

            ObservableList<MidiEventInfo> data = FXCollections.observableArrayList(sortedQueue);
            table.getItems().clear();
            table.setItems(data);
            table.scrollTo(0);
        } else {
            table.getItems().clear();
            contextMenuItemDisable.set(true);
        }

    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return parent;
    }
}
