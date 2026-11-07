package scenes.voice.subpanes;

import entity.voice.Patch;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoiceDetailDialog extends Dialog<Void> {
    private static final Logger logger = LoggerFactory.getLogger(VoiceDetailDialog.class);

    public VoiceDetailDialog() {
        super();

        initStyle(StageStyle.UNDECORATED);
    }

    public VoiceDetailDialog(Patch patch) {
        this();

        getDialogPane().setMinWidth(400);
        getDialogPane().setPrefWidth(400);
        getDialogPane().setMaxWidth(400);

        getDialogPane().setPadding(new Insets(5));

        buildDialog(patch);
    }

    public void buildDialog(Patch patch) {
        logger.debug("Building voice detail dialog");

        Label headerLabel = new Label(patch.getName());
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        getDialogPane().setHeader(headerLabel);

        VoiceDetailPane voiceDetailPane = new VoiceDetailPane(patch);

        getDialogPane().setContent(voiceDetailPane);
        getDialogPane().getButtonTypes().add(ButtonType.OK);

        logger.debug("Built voice detail dialog");
    }
}
