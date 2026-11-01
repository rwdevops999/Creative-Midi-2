package scenes.main;

import javafx.scene.layout.StackPane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.scene.IScene;

@Getter
public class MainScenePane extends StackPane {
    private static final Logger logger = LoggerFactory.getLogger(MainScenePane.class);

    public MainScenePane() {
        super();

        setId("MainScenePane");
    }

    // ACCESSORS
    private IScene parentScene;
    public MainScenePane(IScene ownerScene) {
        this();

        parentScene = ownerScene;

        buildPane();
    }

    private void buildPane() {
        logger.debug("[CM_MAIN_PANE] Building {}", getId());

        getChildren().add(new MainSceneActionsPane(parentScene));

        logger.debug("[CM_MAIN_PANE] Built {}", getId());
    }

}
