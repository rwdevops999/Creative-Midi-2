package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test1Pane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(Test1Pane.class);

    public Test1Pane() {
        super();

        setId("Test1Pane");

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        buildPane();
    }

    private void buildPane() {
        LightIndicatorButton ib = new LightIndicatorButton("TEST");
        getChildren().add(ib);
    }

    private void runTest() {

    }
}
