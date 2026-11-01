package scenes.main;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import util.app.scene.IScene;

public class MainScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(MainScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("Starting MainScene.");

        try {
            return new Base(new MainScenePane(this), callingScene, false);
        } catch (IllegalStateException e) {
            logger.error("Exception. CAUSE: {}", e.getMessage());
        }

        return null;
    }

}
