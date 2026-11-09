package scenes.playlist;

import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import fx.display.EntityListCell;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.SysexListPane;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.statemachine.States;
import util.registry.AppEvent;
import util.registry.Registry;

import java.awt.Container;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class PlaylistListPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistListPane.class);

    public PlaylistListPane() {
        super();

        setId("PlaylistListPane");
    }

    PlaylistPane parent;
    public PlaylistListPane(PlaylistPane owner, double width, double height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showSmallPaneBorder(this, getColor("border", "red"));

        setPaneBackground(this);

        buildPane();
        setListviewItems();

        Registry.getInstance().register(event -> {
            if (event != null && AppEvent.AppEventType.SONG.equals(event.type())) {
                String songName = (String)event.data();

                if (! PlaylistContainer.getInstance().contains(songName)) {
                    DialogFactory.getInstance().renderErrorDialog("Song is not in playlist");
                } else {
                    // This should trigger the change listener in (1)
                    songListView.getSelectionModel().select(PlaylistContainer.getInstance().getSongByName(songName));
                }
            }
        });
    }

    private ListView<Song> songListView;
    private void buildPane() {
        logger.debug("Building {}", getId());

        songListView = new ListView<>();
        songListView.setCellFactory(listView -> new EntityListCell<>());
        VBox.setVgrow(songListView, Priority.ALWAYS);
        getChildren().add(songListView);

        // (1) Song select change listener
        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getPlaylistPane().changeState(States.SELECT);
            getPlaylistPane().getPlaylistDetailsPane().getSongDetailsPane().setSong(newValue);
        });

        logger.debug("Built {}", getId());
    }

    private void setListviewItems() {
        songListView.getItems().clear();
        songListView.setItems(FXCollections.observableArrayList(PlaylistContainer.getInstance().getPlayList()));
    }

    public void reload(Song songToSelect) {
        setListviewItems();
        if (songToSelect != null) {
            songListView.getSelectionModel().select(songToSelect);
        } else {
            songListView.getSelectionModel().clearSelection();
            getPlaylistPane().changeState(States.IDLE);
        }
    }

    // ACCESSORS
    public PlaylistPane getPlaylistPane() {
        return parent;
    }
}
