package fx.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;

import java.util.Optional;

public class DialogFactory {
    private static final Logger logger = LoggerFactory.getLogger(DialogFactory.class);

    private DialogFactory() {
    }

    @Getter
    private static DialogFactory instance = new DialogFactory();

    private Alert renderDialog (String message, Alert.AlertType alertType, String headerText) {
        logger.debug("[CM_DIALOG_FACTORY] Render a uniform dialog. Type = {}", alertType.name());
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(message);

        alert.initOwner(AppInfo.INSTANCE.getPrimaryStage());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();

        return alert;
    }

    public void renderWarningDialog (String message) {
        logger.debug("[CM_DIALOG_FACTORY] Render a WARNING dialog");
        renderDialog(message, Alert.AlertType.WARNING, "Warning");
    }

    public void renderInformationDialog (String message) {
        logger.debug("[CM_DIALOG_FACTORY] Render an INFORMATION dialog");
        renderDialog(message, Alert.AlertType.INFORMATION, "Information");
    }

    public void renderErrorDialog (String message) {
        logger.debug("[CM_DIALOG_FACTORY] Render an ERROR dialog");
        renderDialog(message, Alert.AlertType.ERROR, "Error");
    }

    public boolean renderConfirmationDialog (String header, String message) {
        boolean returnValue = false;
        logger.debug("[CM_DIALOG_FACTORY] Render a CONFIRMATION dialog");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.initOwner(AppInfo.INSTANCE.getPrimaryStage());

        alert.initStyle(StageStyle.UNDECORATED);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            returnValue = true;
        }

        return returnValue;
    }
}
