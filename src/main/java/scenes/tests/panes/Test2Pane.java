package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import static util.Util.setPaneBackground;

public class Test2Pane extends HBox {
    public Test2Pane() {
        super();

        setId("Test2Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        setPadding(new Insets(5));
        setSpacing(5);
        buildPane();
    }

    LightIndicatorButton i1;
    private void buildPane() {
        i1 = new LightIndicatorButton("Inbound", "blue");
        getChildren().add(i1);
    }

    private void runTest() {
        i1.setOnAction(event -> testMidi());
    }

    private void testMidi() {
    }
}
