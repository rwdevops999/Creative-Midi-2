package fx.component;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import util.Util;

import static util.Util.setControlWidthAsPercentage;

public class LabeledSelector extends VBox {
    public LabeledSelector() {
        super();

        setSpacing(2);

        this.setMaxHeight(Double.NEGATIVE_INFINITY);
        this.setMinHeight(Double.NEGATIVE_INFINITY);
        setPadding(new Insets(5));
    }

    public LabeledSelector(Pane parent, String selectorName) {
        this();

        buildComponent(selectorName);
    }

    private void buildComponent(String selectorName) {
        Label selectorLabel = new Label(selectorName);

        ComboBox<String> selectorComboBox = new ComboBox<>();
        selectorComboBox.setMinWidth(0);
        selectorComboBox.prefWidthProperty().bind(this.widthProperty());

        selectorComboBox.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(selectorLabel, selectorComboBox);
    }
}
