package scenes.playlist;

import entity.playlist.Song;
import entity.playlist.SongMapping;
import entity.sysex.Sysex;
import fx.dialog.MappingDialog;
import fx.dialog.SysexMappingDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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

    private final ObjectProperty<ObservableList<Sysex>> preMappingProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private final ObjectProperty<ObservableList<Sysex>> postMappingProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

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
            buildContextMenuForSimpleTable(preMappingTable, song.getPreMappings());
            preMappingTable.itemsProperty().bindBidirectional(preMappingProperty);

            getChildren().add(preMappingTable);

            TableColumn<Sysex, String> preColumn = new TableColumn<>("PRE");
            preColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            preMappingTable.getColumns().add(preColumn);

            TableView<SongMapping> mappingsTable = new TableView<>();
            mappingsTable.setPlaceholder(new Label("No mappings in this song"));
            VBox.setVgrow(mappingsTable, Priority.ALWAYS);
            mappingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//            table.itemsProperty().bindBidirectional(mappingProperty);
            Util.setControlWidthAsPercentage(mappingsTable, this, 54.0);
            getChildren().add(mappingsTable);

            buildColumns(mappingsTable);
//            setMappingData(song);
            buildContextMenu(mappingsTable, song);

            TableView<Sysex> postMappingTable = new TableView<>();
            postMappingTable.setId("Post-Mapping");
            Util.setControlWidthAsPercentage(postMappingTable, this, 22.7);
//            buildContextMenuForView(postMappingView, song);
            getChildren().add(postMappingTable);

            TableColumn<Sysex, String> postColumn = new TableColumn<>("POST");
            postMappingTable.setPlaceholder(new Label("None"));
            postMappingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            postColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            buildContextMenuForSimpleTable(postMappingTable, song.getPostMappings());
            postMappingTable.getColumns().add(postColumn);
            postMappingTable.itemsProperty().bindBidirectional(postMappingProperty);
        }

        logger.debug("Built {}", getId());
    }

    private void buildColumns(TableView<SongMapping> table) {
        TableColumn<SongMapping, String> receiveColumn = new TableColumn<>("Received");
        receiveColumn.setCellValueFactory(new PropertyValueFactory<>("receive"));

        TableColumn<SongMapping, String> replyColumn = new TableColumn<>("Reply");
        replyColumn.setCellValueFactory(new PropertyValueFactory<>("reply"));

        table.getColumns().addAll(receiveColumn, replyColumn);
    }

    private void setMappingData(Song song) {
    }

    private void buildContextMenu(TableView<SongMapping> table, Song song) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add mapping");
        addMenuItem.setOnAction(e -> {
/*            showAddMappingDialog(table, false, song);
            getPlaylistDetailsPane().getSongDetailsPane().updateSongMapping(new ArrayList<>(table.getItems())); */
        });

        MenuItem deleteMenuItem = new MenuItem("Delete mapping");
        deleteMenuItem.setOnAction(e -> {
/*            Mapping mapping = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(mapping);
            getPlaylistDetailsPane().getSongDetailsPane().updateSongMapping(new ArrayList<>(table.getItems())); */
        });
        deleteMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        MenuItem editMenuItem = new MenuItem("Edit mapping");
        editMenuItem.setOnAction(e -> {
/*            Mapping mapping = table.getSelectionModel().getSelectedItem();
            showAddMappingDialog(table, true, mapping);
            getPlaylistDetailsPane().getSongDetailsPane().updateSongMapping(new ArrayList<>(table.getItems())); */
        });
        editMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        contextMenu.getItems().addAll(addMenuItem, deleteMenuItem, editMenuItem);
        table.setContextMenu(contextMenu);
    }

    private void buildContextMenuForSimpleTable(TableView<Sysex> table, List<Sysex> mappings) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add mapping");
        addMenuItem.setOnAction(e -> {
            showAddSysexMappingDialog(table, false, mappings);
        });

        MenuItem deleteMenuItem = new MenuItem("Delete mapping");
        deleteMenuItem.setOnAction(e -> {
            Sysex sysex = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(sysex);
            mappings.remove(sysex);
        });
        deleteMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        MenuItem editMenuItem = new MenuItem("Edit mapping");
        editMenuItem.setOnAction(e -> {
            showAddSysexMappingDialog(table, true, mappings);
        });
        editMenuItem.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        contextMenu.getItems().addAll(addMenuItem, deleteMenuItem, editMenuItem);
        table.setContextMenu(contextMenu);
    }

    public void setSong(Song song) {
        if (song != null) {
            if ("test".equalsIgnoreCase(song.getName())) {
                // Add some dummy premappings
                Sysex sysex1 = SysexContainer.getInstance().getSysexByName("GM1 System On");
                song.getPreMappings().add(sysex1);
                Sysex sysex2 = SysexContainer.getInstance().getSysexByName("Tempo");
                song.getPreMappings().add(sysex2);

                Sysex sysex3 = SysexContainer.getInstance().getSysexByName("All Parameter Reset");
                song.getPostMappings().add(sysex3);
            }

            preMappingProperty.set(FXCollections.observableArrayList(song.getPreMappings()));
            postMappingProperty.set(FXCollections.observableArrayList(song.getPostMappings()));
        }
    }

/*    private void showAddMappingDialog(TableView<SongMapping> table, boolean isUpdate, Song currentSong) {
        new MappingDialog(this, table, isUpdate, currentSong);
    } */

    private void showAddMappingDialog(boolean isUpdate, Song currentSong) {
//        new MappingDialog(this, isUpdate, currentSong);
    }

    private void showAddSysexMappingDialog(TableView<Sysex> table, boolean isUpdate, List<Sysex> existingMappings) {
        new SysexMappingDialog(table, existingMappings, isUpdate);
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
