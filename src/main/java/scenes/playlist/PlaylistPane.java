package scenes.playlist;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import scenes.SceneActionsPane;
import scenes.playlist.consumers.*;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.statemachine.StateMachine;
import scenes.sysex.statemachine.States;

import java.util.function.BiConsumer;

public class PlaylistPane extends BorderPane {
    private PlaylistContainer playlistContainer = PlaylistContainer.getInstance();

    private StateMachine stateMachine = StateMachine.getInstance();

    public PlaylistPane() {
        super();

        setId("PlaylistPane");

        // TODO Register for receiving the song
        if (! playlistContainer.isFileLoaded()) {
            playlistContainer.loadPlaylist();
        }

        buildPane();
    }

    private void buildPane() {
        BiConsumer<NameEntity, Pane>[] consumers = new BiConsumer[]{
                new NewConsumer<Song, Pane>("New"),
                new AddConsumer<Song, Pane>("Add"),
                new DeleteConsumer<Song, Pane>("Delete"),
                new UpdateConsumer<Song, Pane>("Update"),
                new ExecuteConsumer<Song, Pane>("Execute"),
        };

        SysexContainer.getInstance().load();

        SceneActionsPane actionsPane = new SceneActionsPane(this, 20, 100, consumers, this::getEntity);
        stateMachine.setActionsPane(actionsPane);

        PlaylistListPane playlistListPane = new PlaylistListPane(this, 30, 100);
        PlaylistDetailsPane playlistDetailsPane = new PlaylistDetailsPane(this, 50, 100);

        setLeft(playlistListPane);
        setCenter(playlistDetailsPane);
        setRight(actionsPane);

        stateMachine.setState(States.IDLE, !PlaylistContainer.getInstance().isEmpty());
    }

    public void changeState(States state) {
        stateMachine.setState(state, !PlaylistContainer.getInstance().isEmpty());
    }

    public NameEntity getEntity() {
        return getPlaylistDetailsPane().getSongDetailsPane().getSong();
    }

    // ACCESSORS
    public SceneActionsPane getSceneActionsPane() {
        return (SceneActionsPane) getRight();
    }

    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return (PlaylistDetailsPane) getCenter();
    }

    public PlaylistListPane getPlaylistListPane() {
        return (PlaylistListPane) getLeft();
    }
}
