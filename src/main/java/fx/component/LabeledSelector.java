package fx.component;

import entity.NameEntity;
import entity.device.DeviceInfo;
import fx.display.EntityListCell;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
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

        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T device) {
                // Return the property you want the user to see
                return (device == null) ? "" : device.getName();
            }

            @Override
            public T fromString(String string) {
                return null; // Safe to leave as null if the ComboBox is not editable
            }
        });

        getChildren().addAll(selectorLabel, comboBox);
    }

}
