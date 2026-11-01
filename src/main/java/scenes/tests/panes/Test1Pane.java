package scenes.tests.panes;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.HBox;
import util.communication.CommunicationType;
import util.registry.CommunicationMessage;

import static util.Util.setPaneBackground;

public class Test1Pane extends HBox {
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
        LightIndicatorButton i1 = new LightIndicatorButton("Inbound", "blue");
        i1.setOnAction(event -> CommunicationMessage.getInstance().showMessage(CommunicationType.INBOUND, "this is inbound"));
        getChildren().add(i1);

        LightIndicatorButton i2 = new LightIndicatorButton("Outbound", "blue");
        i2.setOnAction(event -> CommunicationMessage.getInstance().showMessage(CommunicationType.OUTBOUND, "this is inbound"));
        getChildren().add(i2);

        LightIndicatorButton i3 = new LightIndicatorButton("Error", "blue");
        i3.setOnAction(event -> CommunicationMessage.getInstance().showMessage(CommunicationType.ERROR, "this is error"));
        getChildren().add(i3);

        LightIndicatorButton i4 = new LightIndicatorButton("Warning", "blue");
        i4.setOnAction(event -> CommunicationMessage.getInstance().showMessage(CommunicationType.WARNING, "this is warning"));
        getChildren().add(i4);

        LightIndicatorButton i5 = new LightIndicatorButton("Info", "blue");
        i5.setOnAction(event -> CommunicationMessage.getInstance().showMessage(CommunicationType.INFO, "this is info"));
        getChildren().add(i5);
    }

    private void runTest() {

    }
}
