package scenes.playlist.consumers;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import javafx.concurrent.Service;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.playlist.service.KeyboardPlayService;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.registry.CommunicationMessage;
import util.registry.Registry;
import util.sysex.SysexWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.util.function.BiConsumer;

import static util.Constants.MONITOR_ACTION_CLEAR;
import static util.communication.CommunicationType.ACTION;

public class ExecuteConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteConsumer.class);

    public ExecuteConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("[CM_EXECUTE_CONSUMER<Playlist>] Handling Startup Playlist Thread");

        SceneActionsPane actionsPane = (SceneActionsPane) pane;

// TODO Uncomment        if (AppInfo.INSTANCE.getSelectedDevice() == null) {
//            DialogFactory.getInstance().renderErrorDialog("Device is not selected");
//        } else {
            Service<Void> service = AppInfo.INSTANCE.getKeyboardService();
            if (service != null) {
                Registry.getInstance().showStatus("Stopped playing the playlist");

                if (service.isRunning()) {
                    service.cancel();
                }

                actionsPane.renameActionButtonName("Stop", "Execute");

                AppInfo.INSTANCE.setKeyboardService(null);
            } else {
                Registry.getInstance().showStatus("Playing the playlist");
                CommunicationMessage.getInstance().showMessage(ACTION, MONITOR_ACTION_CLEAR);
                actionsPane.renameActionButtonName("Execute", "Stop");

                service = new KeyboardPlayService();
                AppInfo.INSTANCE.setKeyboardService(service);
/*                service.start();

                // handle stuff from keyboard service
                service.valueProperty().addListener((observable, oldValue, newValue) -> {
                    // newValue = Shared Entity
                    if (newValue != null) {
                        switch (newValue.getType()) {
                            case OUTGOING -> CommunicationModel.monitorOutbound(newValue.getValue());
                            case SONG_SELECT -> CommunicationModel.monitorSongSelect(newValue.getValue());
                            default -> CommunicationModel.monitorInbound(newValue.getValue());
                        }
                    }
                }); */
//            }
        }
    }
}
