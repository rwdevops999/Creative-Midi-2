package scenes.playlist;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.voice.VoicePane;
import util.app.scene.IScene;
import util.registry.Registry;

public class PlaylistScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("Starting Playlist Search");

        Registry.getInstance().showStatus("Using the playlist");

        return new Base(new PlaylistPane(), callingScene, true);
    }
}
