package scenes.merge_upload;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;
import util.app.scene.IScene;
import util.registry.Registry;

public class MergeUploadScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(MergeUploadScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("Starting Merge and Upload Scene");

        Registry.getInstance().showStatus("Merge and Upload");

        return new Base(new MergeUploadPane(), callingScene, true);
    }
}
