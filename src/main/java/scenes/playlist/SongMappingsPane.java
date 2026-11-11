package scenes.playlist;

import entity.playlist.Song;
import entity.playlist.SongMapping;
import entity.sysex.Sysex;
import fx.dialog.MappingDialog;
import fx.dialog.SysexMappingDialog;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.container.SysexContainer;
import util.Util;

import java.util.ArrayList;
import java.util.List;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SongMappingsPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(SongMappingsPane.class);

    public SongMappingsPane() {
        super();

        setId("SongMappingsPane");
    }

    private PlaylistDetailsPane parent;

    public SongMappingsPane(PlaylistDetailsPane owner, double height) {
        this();

        parent = owner;

        setPaneHeightAsPercentage(this, owner, height);
        showSmallPaneBorder(this, getColor("border", "turquoise"));

        buildPane(owner, new Song());
    }

    private final ListProperty<Sysex> preMappingProperty = new SimpleListProperty<>(FXCollections.observableArrayList());;
    private final ListProperty<Sysex> postMappingProperty = new SimpleListProperty<>(FXCollections.observableArrayList());;
    private final ListProperty<SongMapping> mappingProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Song currentSong;
    public void buildPane(Pane owner, Song song) {
        logger.debug("Building {}", getId());

        getChildren().clear();

        if (getPlaylistDetailsPane().getSongDetailsPane().getSong() != null) {
            TableView<Sysex> preMappingTable = new TableView<>();
            preMappingTable.setId("Pre-Mapping");
            // replace text when no data is available
            preMappingTable.setPlaceholder(new Label("None"));
            // don't show filler column.
            preMappingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            // resize the table
            Util.setControlWidthAsPercentage(preMappingTable, this, 22.7);
            buildContextMenuForSimpleTable(preMappingTable, preMappingProperty);
            preMappingTable.itemsProperty().bindBidirectional(preMappingProperty);

            getChildren().add(preMappingTable);

            TableColumn<Sysex, String> preColumn = new TableColumn<>("PRE");
            preColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            preMappingTable.getColumns().add(preColumn);

            TableView<SongMapping> mappingsTable = new TableView<>();
            mappingsTable.setPlaceholder(new Label("No mappings in this song"));
            VBox.setVgrow(mappingsTable, Priority.ALWAYS);
            mappingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            buildContextMenu(mappingsTable, mappingProperty);
            mappingsTable.itemsProperty().bindBidirectional(mappingProperty);
            Util.setControlWidthAsPercentage(mappingsTable, this, 54.0);
            getChildren().add(mappingsTable);

            buildColumns(mappingsTable);
            setMappingData(song);

            TableView<Sysex> postMappingTable = new TableView<>();
            postMappingTable.setId("Post-Mapping");
            buildContextMenuForSimpleTable(postMappingTable, postMappingProperty);
            Util.setControlWidthAsPercentage(postMappingTable, this, 22.7);
            getChildren().add(postMappingTable);

            TableColumn<Sysex, String> postColumn = new TableColumn<>("POST");
            postMappingTable.setPlaceholder(new Label("None"));
            postMappingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            postColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            postMappingTable.getColumns().add(postColumn);
            postMappingTable.itemsProperty().bindBidirectional(postMappingProperty);
        }

        logger.debug("Built {}", getId());
    }

    private void buildColumns(TableView<SongMapping> table) {
        TableColumn<SongMapping, String> receiveColumn = new TableColumn<>("Received");
        receiveColumn.setCellValueFactory(new PropertyValueFactory<>("receive"));
        receiveColumn.setCellValueFactory(cellData -> {
            // 1. retrieve the selected object from the combo
            var selectedObject = cellData.getValue();

            if (selectedObject != null && selectedObject.getReceive() != null) {
                return new SimpleStringProperty(selectedObject.getReceive().getName());
            } else {
                return new SimpleStringProperty("");
            }
        });

        TableColumn<SongMapping, String> replyColumn = new TableColumn<>("Reply");
        replyColumn.setCellValueFactory(new PropertyValueFactory<>("reply"));
        replyColumn.setCellValueFactory(cellData -> {
            // 1. retrieve the selected object from the combo
            var selectedObject = cellData.getValue();

            if (selectedObject != null && selectedObject.getReply() != null) {
                return new SimpleStringProperty(selectedObject.getReply().getName());
            } else {
                return new SimpleStringProperty("");
            }
        });

        table.getColumns().addAll(receiveColumn, replyColumn);
    }

    private void setMappingData(Song song) {
        ObservableList<SongMapping> tableData = FXCollections.observableArrayList(song.getMappings());
        mappingProperty.set(tableData);
    }

    private void buildContextMenu(TableView<SongMapping> table, ListProperty<SongMapping> mappingProperty) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add mapping");
        addMenuItem.setOnAction(e -> {
            showAddMappingDialog(table, false, mappingProperty);
        });

        MenuItem deleteMenuItem = new MenuItem("Delete mapping");
        deleteMenuItem.setOnAction(e -> {
            SongMapping mapping = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(mapping);
            mappingProperty.get().remove(mapping);
        });
        deleteMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        MenuItem editMenuItem = new MenuItem("Edit mapping");
        editMenuItem.setOnAction(e -> {
            showAddMappingDialog(table, true, mappingProperty);
        });
        editMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        contextMenu.getItems().addAll(addMenuItem, deleteMenuItem, editMenuItem);
        table.setContextMenu(contextMenu);
    }

    private void buildContextMenuForSimpleTable(TableView<Sysex> table, ListProperty<Sysex> mappingProperty) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add mapping");
        addMenuItem.setOnAction(e -> {
            showAddSysexMappingDialog(table, false, mappingProperty);
        });

        MenuItem deleteMenuItem = new MenuItem("Delete mapping");
        deleteMenuItem.setOnAction(e -> {
            Sysex sysex = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(sysex);
            mappingProperty.set(FXCollections.observableArrayList(table.getItems()));
        });
        deleteMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        MenuItem editMenuItem = new MenuItem("Edit mapping");
        editMenuItem.setOnAction(e -> {
            showAddSysexMappingDialog(table, true, mappingProperty);
        });
        editMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        contextMenu.getItems().addAll(addMenuItem, deleteMenuItem, editMenuItem);
        table.setContextMenu(contextMenu);
    }

    private boolean isUpdating = false;
    public void setSong(Song song) {
        currentSong = song;

        if (song != null) {
            isUpdating = true;
            if (song.getPreMappings() != null) {
                preMappingProperty.set(FXCollections.observableArrayList(song.getPreMappings()));
                preMappingProperty.addListener((ListChangeListener<Sysex>) change -> {
                    if (! isUpdating) {
                        currentSong.setPreMappings(new ArrayList<>(preMappingProperty.get()));
                        getPlaylistDetailsPane().getSongDetailsPane().setSong(currentSong);
                    }
                });
            }

            if (song.getPostMappings() != null) {
                postMappingProperty.set(FXCollections.observableArrayList(song.getPostMappings()));
                postMappingProperty.addListener((ListChangeListener<Sysex>) change -> {
                    if (! isUpdating) {
                        currentSong.setPostMappings(new ArrayList<>(postMappingProperty.get()));
                        getPlaylistDetailsPane().getSongDetailsPane().setSong(currentSong);
                    }
                });
            }

            if (song.getMappings() != null) {
                mappingProperty.set(FXCollections.observableArrayList(song.getMappings()));
                mappingProperty.addListener((ListChangeListener<SongMapping>) change -> {
                    if (! isUpdating) {
                        currentSong.setMappings(new ArrayList<>(mappingProperty.get()));
                        getPlaylistDetailsPane().getSongDetailsPane().setSong(currentSong);
                    }
                });
            }
            isUpdating = false;
        }
    }

    private void showAddMappingDialog(TableView<SongMapping> table, boolean isUpdate, ListProperty<SongMapping> mappingProperty) {
        isUpdating = true;
        new MappingDialog(table, mappingProperty, isUpdate);
        isUpdating = false;
    }

    private void showAddSysexMappingDialog(TableView<Sysex> table, boolean isUpdate, ListProperty<Sysex> mappingProperty) {
        isUpdating = true;
        new SysexMappingDialog(table, mappingProperty, isUpdate);
        isUpdating = false;
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
