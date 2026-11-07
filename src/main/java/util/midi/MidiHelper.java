package util.midi;

import com.sun.jdi.ByteType;
import entity.midi.Midi;
import entity.voice.Patch;
import fx.component.ChannelIndicator;
import fx.dialog.DialogFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ToggleButton;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.eventhandlers.IThreadEventHandler;
import util.voice.receiver.MultiChannelMidiReceiver;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;

import static util.Constants.*;
import static util.app.colors.ColorScheme.getColor;

public class MidiHelper {
    private static final Logger logger = LoggerFactory.getLogger(MidiHelper.class);

    @Getter
    private static final MidiHelper instance = new MidiHelper();

    @Getter
    private final Map<Integer, ChannelIndicator> ledMap = new HashMap<>();
    @Getter
    private final Map<Integer, BooleanProperty> listen = new HashMap<>();

    private static final MidiWriter midiWriter = MidiWriter.getInstance();

    private MidiHelper() {
        ledMap.put(CHANNEL_RIGHT1, new ChannelIndicator("Ch 1:", getColor("voice", "ledRed", "on"), getColor("voice", "ledRed", "off")));
        ledMap.put(CHANNEL_LEFT, new ChannelIndicator("Ch 2 (⇧):", getColor("voice", "ledGreen", "on"), getColor("voice", "ledGreen", "off")));
        ledMap.put(CHANNEL_RIGHT2, new ChannelIndicator("Ch 3 (⌃):", getColor("voice", "ledBlue", "on"), getColor("voice", "ledBlue", "off")));

        listen.put(CHANNEL_RIGHT1, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_LEFT, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_RIGHT2, new SimpleBooleanProperty(true));
    }

    public boolean sendAsMidi(int channel, Patch patch) {
        boolean result = true;

        try {
            Midi midi = new Midi("Bank Select MSB", channel, ShortMessage.CONTROL_CHANGE, 0x0, patch.getMsb());
            midiWriter.send(midi);

            midi = new Midi("Bank Select LSB", channel, ShortMessage.CONTROL_CHANGE, 0x20, patch.getLsb());
            midiWriter.send(midi);

            midi = new Midi("Program Change", channel, ShortMessage.PROGRAM_CHANGE, patch.getPc());
            midiWriter.send(midi);

        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
            result = false;
        }

        return result;
    }

    public IThreadEventHandler successHandler = new IThreadEventHandler() {
        @Override
        public void handle(ToggleButton button) {
            if (button != null) {
                button.setSelected(false);
            }
        }
    };

    public IThreadEventHandler interruptedHandler = new IThreadEventHandler() {
        @Override
        public void handle(ToggleButton button) {
            if (button != null) {
                button.setSelected(false);
            }
        }
    };

    public boolean playMidiFile (MultiChannelMidiReceiver multiChannelMidiReceiver, ToggleButton btn) {
        if (AppInfo.INSTANCE.getSelectedDevice() == null) {
            DialogFactory.getInstance().renderWarningDialog("No device selected");
        } else {
            MidiDevice inputDevice = AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();
            if (inputDevice == null) {
                DialogFactory.getInstance().renderWarningDialog("Input device not ready");
                return false;
            } else {
                MidiPlayer.playMidi(successHandler, interruptedHandler, multiChannelMidiReceiver, btn);

                return true;
            }
        }

        return false;
    }

    public boolean isMidiPlaying() {
        return MidiPlayer.isPlaying();
    }

    public void stopMidiFile (Map<Integer, ChannelIndicator> midiChannelIndicators) {
        MidiPlayer.stopMidi(midiChannelIndicators);
    }

}
