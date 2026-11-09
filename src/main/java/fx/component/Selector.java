package fx.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.List;

public class Selector<T> extends HBox {
    private final ObjectProperty<T> selected = new SimpleObjectProperty<>();

    public T getSelectedItem() {
        return selected.get();
    }

    public Selector(String label, List<T> input, String selectedItem) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(5);

        if (label != null) {
            Label selectLabel = new Label(label);
            this.getChildren().add(selectLabel);
        }

        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(input);
        comboBox.valueProperty().bindBidirectional(selected);
        if (selectedItem != null) {
            this.selected.setValue((T)selectedItem);
        }
        this.getChildren().add(comboBox);
    }
}
