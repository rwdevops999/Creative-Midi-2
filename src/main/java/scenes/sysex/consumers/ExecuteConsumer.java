package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.midi.MidiWriter;
import util.registry.Registry;
import util.sysex.SysexWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.util.function.BiConsumer;

public class ExecuteConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteConsumer.class);

    public ExecuteConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Sysex sysex = (Sysex) entity;
        if (sysex.getContent().getSysex().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to execute an empty sysex");
        } else {
            if (AppInfo.INSTANCE.getSelectedDevice() != null) {
                Registry.getInstance().showStatus("Sending sysex");
                try {
                    SysexWriter.getInstance().send(sysex);
                } catch (InvalidMidiDataException | MidiUnavailableException e) {
                    logger.debug("Exception. CAUSE {}", e.getMessage());
                }
            } else {
                DialogFactory.getInstance().renderErrorDialog("No device selected for sending SYSEX");
            }
        }
    }
}
