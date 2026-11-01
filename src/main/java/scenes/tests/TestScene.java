package scenes.tests;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import util.app.scene.IScene;

public class TestScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(TestScene.class);

    @Override
    public Pane renderScene(IScene callingScene, Pane pane) {
        logger.debug("[CM_TEST_SCENE] Starting Test Scene with a pane");

        return new Base(pane, callingScene, true);
    }
}
