package fx.dialog;

import entity.midi.MidiEventInfo;
import fx.dialog.content.EventDetailPane;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDetailDialog extends Dialog<Void> {
    private static final Logger logger = LoggerFactory.getLogger(EventDetailDialog.class);

    private static final int DIALOG_WIDTH = 600;

    public EventDetailDialog() {
        super();

        initStyle(StageStyle.UNDECORATED);
    }

    public EventDetailDialog(MidiEventInfo eventInfo) {
        this();

        getDialogPane().setMinWidth(DIALOG_WIDTH);
        getDialogPane().setPrefWidth(DIALOG_WIDTH);
        getDialogPane().setMaxWidth(DIALOG_WIDTH);

        getDialogPane().setPadding(new Insets(5));

        buildDialog(eventInfo);
    }

    public void buildDialog(MidiEventInfo eventInfo) {
        logger.debug("Building event detail dialog");

        Label headerLabel = new Label(eventInfo.getDescription());
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        getDialogPane().setHeader(headerLabel);

        EventDetailPane eventDetailPane = new EventDetailPane(eventInfo);

        getDialogPane().setContent(eventDetailPane);
        getDialogPane().getButtonTypes().add(ButtonType.OK);

        logger.debug("Built voice detail dialog");
    }
}
