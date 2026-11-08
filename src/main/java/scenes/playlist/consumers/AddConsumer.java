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

import java.util.function.BiConsumer;

public class AddConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(AddConsumer.class);

    public AddConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Song song = new Song((Song) entity);
        if (song.getName().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to add a song with empty name");
        } else {
            PlaylistContainer.getInstance().addSong(song);

            SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
            PlaylistPane playListPane = sceneActionsPane.getPane();
            playListPane.getPlaylistListPane().reload(song);
        }
    }
}
