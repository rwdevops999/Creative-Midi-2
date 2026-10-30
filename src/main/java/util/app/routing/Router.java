package util.app.routing;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.app.scene.IScene;

public class Router implements IRouter {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    @Getter
    private static Router instance = new Router();

    private Router() {
    }

    @Override
    public void routeTo(IScene callingScene, IScene calledScene, boolean cleanup) {
        logger.debug("[CM_ROUTER] Route To {}", calledScene.getClass().getName());
        Pane spaOwner = AppInfo.INSTANCE.getSpaOwner();

        if (cleanup)
        {
            Pane spa = AppInfo.INSTANCE.getSpa();
            if (spa != null) {
                spaOwner.getChildren().remove(spa);
            }
        }

        Pane pane = calledScene.render(callingScene);
        if (pane != null) {
            if (spaOwner instanceof BorderPane bp) {
                bp.setCenter(pane);
            } else {
                spaOwner.getChildren().add(pane);
            }
        }
    }

}
