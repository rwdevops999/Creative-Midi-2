package scenes.eventlist.editor;

import entity.events.FilterBlock;
import entity.midi.MidiEventInfo;
import fx.dialog.DialogFactory;
import fx.dialog.EventDetailDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.base.BaseClosePane;
import util.app.AppInfo;
import util.app.colors.ColorScheme;
import util.midi.EventKeyType;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

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

    private FilterBlock filterBlock;

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
            List<MidiEventInfo> selectedEvents = table.getSelectionModel().getSelectedItems();

            for (MidiEventInfo selectedEvent : selectedEvents) {
                deleteEvent(selectedEvent, currentSequence);
            }

            getEventListEditorPane().getEventListPane().updateSequence(currentSequence);
            executeFiltering();

            AppInfo.INSTANCE.setMidifileChanged(true);

            midiFileHasChangedCallback();
        });
        contextMenu.getItems().add(deleteEvent);

        SeparatorMenuItem sep = new SeparatorMenuItem();
        contextMenu.getItems().add(sep);

        MenuItem infoEvent = new MenuItem("Info");
        infoEvent.disableProperty().bindBidirectional(contextMenuItemDisable);
        infoEvent.setOnAction(e -> {
            List<MidiEventInfo> selectedEvents = table.getSelectionModel().getSelectedItems();

            if (!selectedEvents.isEmpty()) {
                EventDetailDialog dialog = new EventDetailDialog(selectedEvents.get(0));
                dialog.showAndWait();
            }
        });
        contextMenu.getItems().add(infoEvent);

        table.setContextMenu(contextMenu);
    }

    public void deleteEvent(MidiEventInfo eventInfoToDelete, Sequence activeSequence) {
        currentEvents.remove(eventInfoToDelete);

        MidiEvent targetEvent = eventInfoToDelete.getOriginalEvent();
        if (targetEvent == null || targetEvent.getMessage() == null) return;

        System.out.println("Info tick: " + eventInfoToDelete.getTick());
        System.out.println("Actual event tick: " + targetEvent.getTick());
        System.out.println("Message type: " + targetEvent.getMessage().getClass().getSimpleName());

        // 1. Always remove the exact original event first
        for (Track track : activeSequence.getTracks()) {
            if (track.remove(targetEvent)) {
                break;
            }
        }

        // 2. If it's a channel message, also clean up nearby MSB/LSB/PC events on that channel
        if (targetEvent.getMessage() instanceof ShortMessage targetSm) {
            int targetChannel = targetSm.getChannel();

            // Use the actual tick from the event, not a guessed multiplier
            long startTick = targetEvent.getTick();
            long endTickWindow = startTick + 5;

            for (Track track : activeSequence.getTracks()) {
                List<MidiEvent> eventsToRemove = new ArrayList<>();

                for (int i = 0; i < track.size(); i++) {
                    MidiEvent currentEvent = track.get(i);
                    long currentTick = currentEvent.getTick();

                    if (currentTick >= startTick && currentTick <= endTickWindow) {
                        if (currentEvent.getMessage() instanceof ShortMessage currentSm
                                && currentSm.getChannel() == targetChannel) {

                            int cmd = currentSm.getCommand();
                            boolean isMSB = (cmd == ShortMessage.CONTROL_CHANGE && currentSm.getData1() == 0);
                            boolean isLSB = (cmd == ShortMessage.CONTROL_CHANGE && currentSm.getData1() == 32);
                            boolean isPC  = (cmd == ShortMessage.PROGRAM_CHANGE);

                            if (isMSB || isLSB || isPC) {
                                eventsToRemove.add(currentEvent);
                            }
                        }
                    }
                }

                for (MidiEvent event : eventsToRemove) {
                    track.remove(event);
                }
            }
        }
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
            if (filterBlock != null) {
                /*
                 * Filter on channels using the pattern
                 */
                if (filterBlock.getFilterChannels() != 0) {
                    int check = 0x1;
                    for (int i = 0; i < 16; i++) {
                        final int val = i+1;
                        boolean filterOut = (filterBlock.getFilterChannels() & check) != 0;
                        if (filterOut) {
                            filteredEvents = filteredEvents.stream().filter(e -> e.getChannel() == null || (e.getChannel() != val)).toList();
                        }

                        check <<= 1;
                    }
                }

                if (filterBlock.isFilterMidiOut()) {
                    filteredEvents = filteredEvents.stream().filter(e -> ! e.getRootEventKey().equals(EventKeyType.MIDI)).toList();
                } else {
                    List<EventKeyType> list = filterBlock.getEventsToFilter("midi");
                    if (! list.isEmpty()) {
                        filteredEvents = filteredEvents.stream().filter(e -> ! list.contains(e.getEventEventKey())).toList();
                    }
                }

                if (filterBlock.isFilterMetaOut()) {
                    filteredEvents = filteredEvents.stream().filter(e -> ! e.getRootEventKey().equals(EventKeyType.META)).toList();
                } else {
                    List<EventKeyType> list = filterBlock.getEventsToFilter("meta");
                    if (! list.isEmpty()) {
                        filteredEvents = filteredEvents.stream().filter(e -> ! list.contains(e.getEventEventKey())).toList();
                    }
                }

                if (filterBlock.isFilterSysexOut()) {
                    filteredEvents = filteredEvents.stream().filter(e -> ! e.getRootEventKey().equals(EventKeyType.SYSEX)).toList();
                } else {
                    List<EventKeyType> list = filterBlock.getEventsToFilter("sysex");
                    if (! list.isEmpty()) {
                        filteredEvents = filteredEvents.stream().filter(e -> ! list.contains(e.getEventEventKey())).toList();
                    }
                }
            }

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

    public void filter(FilterBlock filterBlock) {
        this.filterBlock = filterBlock;

        executeFiltering();
    }

    private final EventHandler handler = event -> {
        if (AppInfo.INSTANCE.isMidifileChanged()) {
            if (DialogFactory.getInstance().renderConfirmationDialog("Midi File Changed", "Midi file has changed. Save?")) {
                getEventListEditorPane().getEventListPane().saveMidiFile();

                AppInfo.INSTANCE.setMidifileChanged(false);
            }
        }
    };

    private void midiFileHasChangedCallback() {
        Base base = (Base) AppInfo.INSTANCE.getSpa();
        BaseClosePane closePane = base.getClosePane();

        closePane.setActionHandler(handler);

        // Also activate the save button above
        getEventListEditorPane().getEventListPane().fileHasChanged();
    }

    // ACCESSORS
    public EventListEditorPane getEventListEditorPane() {
        return parent;
    }
}
