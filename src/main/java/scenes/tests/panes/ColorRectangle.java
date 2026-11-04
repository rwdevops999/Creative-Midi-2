package scenes.tests.panes;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import util.Util;

public class ColorRectangle extends HBox {
    public ColorRectangle(Color color, ObjectProperty<Color> dsColor) {
        super();

        setMaxSize(200, 50);
        setMinSize(200, 50);
        setPrefSize(200, 50);

        setBackground(Background.fill(color));

        Util.showSmallPaneBorder(this, Color.BLACK);
        buildPane(dsColor);

        setAlignment(Pos.CENTER_LEFT);
    }

    private void buildPane(ObjectProperty<Color> dsColor) {
        Label testLabel = new Label("120 bpm");
        testLabel.textFillProperty().bind(dsColor);
        getChildren().add(testLabel);
    }
}
