package scenes.playlist.consumers;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.playlist.PlaylistPane;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;

import java.util.function.BiConsumer;

public class DeleteConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteConsumer.class);

    public DeleteConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Song song = (Song) entity;
        PlaylistContainer.getInstance().delete(song);

        SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
        PlaylistPane playlistPane = sceneActionsPane.getPane();
        playlistPane.getPlaylistListPane().reload(null);
    }
}
