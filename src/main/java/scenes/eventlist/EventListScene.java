package scenes.eventlist;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;
import util.app.scene.IScene;
import util.registry.Registry;

public class EventListScene implements IScene {
    private static final Logger logger = LoggerFactory.getLogger(EventListScene.class);

    @Override
    public Pane render(IScene callingScene) {
        logger.debug("Starting EventList Scene");

        Registry.getInstance().showStatus("Edit events");

        return new Base(new EventListPane(), callingScene, true);
    }
}
