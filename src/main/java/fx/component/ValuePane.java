package fx.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import static util.Util.setPaneWidthAsPercentage;

public class ValuePane extends GridPane {
    public ValuePane() {
        super();

        setVgap(10);
        setAlignment(Pos.CENTER_LEFT);

        int[] columnSizes = {20,20, 60};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

    }

    private final StringProperty inputValue = new SimpleStringProperty("" + 0);
    private StringProperty linkedProperty = new SimpleStringProperty();
    public ValuePane(Pane owner, String label, String defaultValue) {
        this();

        inputValue.setValue(defaultValue);

        setPaneWidthAsPercentage(this, owner, 100);

        buildPane(label);
    }

    private void buildPane(String label) {
        Label labelText = new Label(label);
        add(labelText, 0, 0);

        TextField searchInputField = new TextField();
        searchInputField.textProperty().bindBidirectional(inputValue);
        searchInputField.setOnAction(
                e ->  linkedSearchButton.fire());

        searchInputField.textProperty().addListener((observable, oldValue, newValue) -> linkedProperty.set(newValue));

        add(searchInputField, 1, 0);
    }

    private Button linkedSearchButton;
    public void addLinkedSearchButton(Button button) {
        linkedSearchButton = button;
    }

    public void addLinkedProperty(StringProperty property) {
        linkedProperty = property;
    }

    public void setInputValue(String value) {
        inputValue.set(value);
    }
}
