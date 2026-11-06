package fx.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import lombok.Setter;
import scenes.voice.VoiceSearchSelectionPane;

import static util.Util.setPaneWidthAsPercentage;

public class DynamicPane extends GridPane {
    @Getter
    @Setter
    private int buttonId;

    private final StringProperty inputValue = new SimpleStringProperty();

    private VoiceSearchSelectionPane parent;
    public DynamicPane(VoiceSearchSelectionPane owner, int id) {
        super();

        parent = owner;

        this.buttonId = id;
        setPaneWidthAsPercentage(this, owner, 100);

        int[] columnSizes = {12,3,50,3,12,20};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        String flatButtonStyle =
                "-fx-background-color: #a9a9a9; " +          // Gray background (or 'transparent')
                        "-fx-background-radius: 0; " +       // Square corners
                        "-fx-text-fill: #333333; " +         // text color
                        "-fx-font-size: 14px; " +            // text size
                        "-fx-font-weight: bold; " +          // bold text
                        "-fx-cursor: hand;";

        int row = 0;
        // ROW (+, input, -)

        Button addButton = new Button("+");
        addButton.setStyle(flatButtonStyle);
        add(addButton, 0, row);
        addButton.setOnAction(event -> {
            Button btn = (Button) event.getSource();
            btn.setDisable(true);
            getVoiceSearchSelectionPane().addDynamicPane(buttonId);
            btn.setDisable(false);
        });

        TextField searchInputField = new TextField();
        if (buttonId == 0) {
            searchInputField.setPromptText("search for ...");
        } else {
            searchInputField.setPromptText("and ...");
        }
        searchInputField.textProperty().bindBidirectional(inputValue);
        searchInputField.setOnAction(e -> linkedSearchButton.fire());

        add(searchInputField, 2, row, 3, 1);

        Button removeButton = new Button("-");
        removeButton.setStyle(flatButtonStyle);
        add(removeButton, 5, row);
        removeButton.setOnAction(event -> {
            Button btn = (Button) event.getSource();
            btn.setDisable(true);
            getVoiceSearchSelectionPane().removeDynamicPane(buttonId);
            btn.setDisable(false);
        });
    }

    private Button linkedSearchButton;
    public void addLinkedSearchButton(Button button) {
        linkedSearchButton = button;
    }

    public String getInputValue() {
        return inputValue.get();
    }

    // ACCESSORS

    public VoiceSearchSelectionPane getVoiceSearchSelectionPane() {
        return parent;
    }
}
