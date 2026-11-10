package scenes.playlist.consumers;

import entity.NameEntity;
import entity.playlist.Song;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.playlist.PlaylistPane;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.statemachine.States;
import util.app.AppInfo;

import java.util.function.BiConsumer;

public class NewConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(NewConsumer.class);

    public NewConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Song song = new Song();
        song.setId(PlaylistContainer.getInstance().getNextSongId());

        SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
        PlaylistPane playlistPane = sceneActionsPane.getPane();

        playlistPane.getPlaylistDetailsPane().getSongDetailsPane().setSong(song);
        playlistPane.changeState(States.NEW);
    }
}
