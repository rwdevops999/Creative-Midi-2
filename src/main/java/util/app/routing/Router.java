package util.app.routing;

import lombok.Getter;
import util.app.scene.IScene;

public class Router implements IRouter {

    @Getter
    private static Router instance = new Router();

    private Router() {
    }

    @Override
    public void routeTo(IScene callingScene, IScene calledScene, boolean cleanup) {
    }
}
