package fx.component;

import entity.NameEntity;
import fx.display.EntityListCell;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import util.Util;

import static util.Util.setControlWidthAsPercentage;

public class LabeledSelector<T extends NameEntity> extends VBox {
    public LabeledSelector() {
        super();

        setSpacing(2);

        this.setMaxHeight(Double.NEGATIVE_INFINITY);
        this.setMinHeight(Double.NEGATIVE_INFINITY);
        setPadding(new Insets(5));
    }

    public LabeledSelector(Pane parent, String selectorName) {
        this();

        buildComponent(parent, selectorName);
    }

    ComboBox<T> comboBox;
    private void buildComponent(Pane parent, String selectorName) {
        Label selectorLabel = new Label(selectorName);

        comboBox = new ComboBox<>();
        comboBox.setMinWidth(100);

        // Vul de volledige breedte van DEZE VBox (die door de parent al op 50% is gezet)
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.prefWidthProperty().bind(this.widthProperty());

        comboBox.setCellFactory(listView -> new EntityListCell<>());
        comboBox.setButtonCell(new EntityListCell<>());

        getChildren().addAll(selectorLabel, comboBox);
    }

    public ComboBox<T> getComboBox() {
        return comboBox;
    }
}
