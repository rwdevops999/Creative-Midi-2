package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneBackground;

public class Test1Pane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(Test1Pane.class);

    public Test1Pane() {
        super();

        setId("Test1Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        setSpacing(5);
        buildPane();
    }

    private void buildPane() {
        String light = "green";

        LightIndicatorButton ibR1 = new LightIndicatorButton("Right1", "blue");
        getChildren().add(ibR1);

        LightIndicatorButton ibL = new LightIndicatorButton(" Left ", "red");
        getChildren().add(ibL);

        LightIndicatorButton ibR2 = new LightIndicatorButton("Right2", "green");
        getChildren().add(ibR2);
    }

    private void runTest() {

    }
}
