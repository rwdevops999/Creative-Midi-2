package scenes.main;

import app.root.root.MainActionsPane;
import fx.component.SceneSelectButton;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.SysexScene;
import util.app.scene.IScene;

public class MainSceneActionsPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(MainSceneActionsPane.class);

    public MainSceneActionsPane() {
        super();

        setId("MainSceneActionsPane");

        int totalColumns = 10;
        double percentagePerColumn = 100.0 / totalColumns; // Dit is ~8.3333%

        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentagePerColumn);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        setHgap(10);
        setVgap(10);

        setPadding(new Insets(10));
    }

    private IScene parentScene;
    public MainSceneActionsPane(IScene ownerScene) {
        this();

        this.parentScene = ownerScene;

        buildPane();
    }

    private static final int ICON_SIZE=64;

    private void buildPane() {
        logger.debug("Building {}", getId());

        add(new SceneSelectButton(parentScene, ICON_SIZE, "sysexButton", "handle sysex", "sysex", new SysexScene()), 1, 0);

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public IScene getParentScene() {
        return parentScene;
    }
}
