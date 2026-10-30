package scenes.base;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.app.scene.IScene;

import static util.Constants.SCENE_SPA;

public class Base extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(Base.class);

    public Base() {
        super();
    }

    public Base(Pane content, IScene callingScene, boolean addClosePane) {
        this();

        setId(SCENE_SPA);


        AppInfo.INSTANCE.setSpa(this);

        logger.debug("[CM_BASE] Building {}", getId());

        if (content != null) {
            setCenter(content);
        }

//        if (addClosePane) {
//            setBottom(new BaseClosePane(callingScene, null));
//        }

        logger.debug("[CM_BASE] Built {}", getId());
    }
}
