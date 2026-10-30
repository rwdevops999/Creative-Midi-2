package scenes.main;

import app.root.root.MainActionsPane;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public IScene getParentScene() {
        return parentScene;
    }
}
