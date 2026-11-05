package scenes.voice;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;
import util.app.scene.IScene;
import util.registry.Registry;

public class VoiceScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(VoiceScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("Starting Voice Search");

        Registry.getInstance().showStatus("Search for voices");

        return new Base(new VoicePane(), callingScene, true);
    }
}
