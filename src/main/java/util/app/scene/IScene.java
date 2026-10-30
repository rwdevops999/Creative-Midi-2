package util.app.scene;

import javafx.scene.layout.Pane;

public interface IScene {
    default Pane render(IScene callingScene) {
        return null;
    }

    default Pane renderScene(IScene callingScene, Pane pane) {
        return null;
    }

}
