package scenes.playlist.consumers;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.playlist.PlaylistPane;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;
import util.app.AppInfo;

import java.awt.Container;
import java.util.function.BiConsumer;

public class UpdateConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConsumer.class);

    public UpdateConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Song song = new Song((Song) entity);

        if (song.getName().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to update a song with empty name");
        } else {
            Song originalSong = AppInfo.INSTANCE.getCurrentSong();
            PlaylistContainer.getInstance().replace(originalSong, song);

            SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
            PlaylistPane playlistPane = sceneActionsPane.getPane();
            playlistPane.getPlaylistListPane().reload(song);
        }
    }
}
