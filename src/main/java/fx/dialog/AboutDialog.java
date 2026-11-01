package fx.dialog;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

public class AboutDialog extends Dialog<Void> {
    public AboutDialog(String version) {

        super();

        initStyle(StageStyle.UNDECORATED);

        setTitle("About Creative Midi");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        content.getChildren().add(new Label("Creative Midi Version : " + version));

        getDialogPane().setContent(content);
    }
}
