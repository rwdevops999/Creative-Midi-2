package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.communication.CommunicationType;
import util.registry.CommunicationMessage;

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
        ibR1.setOnAction(event -> {
//            CommunicationMessage.getInstance().showMessage(CommunicationType.INBOUND, "this is inbound");
            CommunicationMessage.getInstance().showMessage(CommunicationType.ERROR, "this is error");
        });
        getChildren().add(ibR1);

        LightIndicatorButton ibL = new LightIndicatorButton(" Left ", "red");
        ibL.setOnAction(event -> {
//            CommunicationMessage.getInstance().showMessage(CommunicationType.OUTBOUND, "this is outbound");
            CommunicationMessage.getInstance().showMessage(CommunicationType.WARNING, "this is warning");
        });
        getChildren().add(ibL);

        LightIndicatorButton ibR2 = new LightIndicatorButton("Right2", "green");
        ibR2.setOnAction(event -> {
            CommunicationMessage.getInstance().showMessage(CommunicationType.INFO, "this is info");
        });
        getChildren().add(ibR2);
    }

    private void runTest() {

    }
}
