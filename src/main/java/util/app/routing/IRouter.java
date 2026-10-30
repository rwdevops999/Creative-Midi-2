package util.app.routing;

import util.app.scene.IScene;

public interface IRouter {
    void routeTo (IScene callingScene, IScene calledScene, boolean cleanup);
}
