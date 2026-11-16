package fx.dialog;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class NameDialog extends Dialog<String> {
    public StringProperty nameProperty = new SimpleStringProperty();

    public NameDialog(String purpose, String defaultName) {
        super();

        setTitle("Enter " + purpose + " name");

        if (defaultName != null) {
            nameProperty.setValue(defaultName);
        }

        ButtonType useButtonType = new ButtonType("Use", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(useButtonType, ButtonType.CANCEL);

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);

        Label nameLabel = new Label("Name");
        content.getChildren().add(nameLabel);

        TextField nameTextField = new TextField();
        nameTextField.setPromptText("name ...");
        nameTextField.textProperty().bindBidirectional(nameProperty);
        content.getChildren().add(nameTextField);

        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == useButtonType) {
                return nameProperty.get();
            }

            return null; // Geeft null terug bij annuleren
        });

        setOnShowing(event -> {
            Platform.runLater(nameTextField::requestFocus);
        });

    }
}
