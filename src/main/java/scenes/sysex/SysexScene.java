package scenes.sysex;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import util.app.scene.IScene;

public class SysexScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(SysexScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("[CM_SYSEX_SCENE] Starting Sysex Scene");

        return new Base(new SysexPane(), callingScene, true);
    }
}
