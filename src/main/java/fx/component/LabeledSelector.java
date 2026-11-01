package fx.component;

import entity.NameEntity;
import fx.display.EntityListCell;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import lombok.Getter;

@Getter
public class LabeledSelector<T extends NameEntity> extends VBox {
    public LabeledSelector() {
        super();

        setSpacing(2);

        this.setMaxHeight(Double.NEGATIVE_INFINITY);
        this.setMinHeight(Double.NEGATIVE_INFINITY);
        setPadding(new Insets(5));
    }

    public LabeledSelector(String selectorName) {
        this();

        buildComponent(selectorName);
    }

    ComboBox<T> comboBox;
    private void buildComponent(String selectorName) {
        Label selectorLabel = new Label(selectorName);

        comboBox = new ComboBox<>();
        comboBox.setMinWidth(100);

        // Fill the complete width of this VBox (which is already set to 50% by the parent)
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.prefWidthProperty().bind(this.widthProperty());

        comboBox.setCellFactory(listView -> new EntityListCell<>());

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // create the cell and JavaFX links the 'newVal' to it.
                comboBox.setButtonCell(new EntityListCell<>());
            }
        });

        getChildren().addAll(selectorLabel, comboBox);
    }

}
