package util.midi;

import com.sun.jdi.ByteType;
import entity.midi.Midi;
import entity.midi.MidiEventInfo;
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
import util.midi.eventprocessors.MetaProcessor;
import util.voice.receiver.MultiChannelMidiReceiver;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Constants.*;
import static util.app.colors.ColorScheme.getColor;
import static util.midi.EventKeyType.*;

public class MidiHelper {
    private static final Logger logger = LoggerFactory.getLogger(MidiHelper.class);

    @Getter
    private static final MidiHelper instance = new MidiHelper();

    @Getter
    private final Map<Integer, ChannelIndicator> ledMap = new HashMap<>();
    @Getter
    private final Map<Integer, BooleanProperty> listen = new HashMap<>();

    private static final MidiWriter midiWriter = MidiWriter.getInstance();
    private static final MetaProcessor metaProcessor = MetaProcessor.getInstance();

    private MidiHelper() {
        ledMap.put(CHANNEL_RIGHT1, new ChannelIndicator("Ch 1:", getColor("voice", "ledRed", "on"), getColor("voice", "ledRed", "off")));
        ledMap.put(CHANNEL_LEFT, new ChannelIndicator("Ch 2 (⇧):", getColor("voice", "ledGreen", "on"), getColor("voice", "ledGreen", "off")));
        ledMap.put(CHANNEL_RIGHT2, new ChannelIndicator("Ch 3 (⌃):",getColor("voice", "ledBlue", "on"), getColor("voice", "ledBlue", "off")));
        ledMap.put(CHANNEL_STYLE, new ChannelIndicator("Style", getColor("voice", "ledOrange", "on"), getColor("voice", "ledOrange", "off").darker()));

        listen.put(CHANNEL_RIGHT1, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_LEFT, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_RIGHT2, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_STYLE, new SimpleBooleanProperty(true));
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

    public boolean sendMidi(Midi midi) {
        boolean result = true;

        try {
            midiWriter.send(midi);
        } catch (MidiUnavailableException e) {
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

    public boolean playMidiFile (MultiChannelMidiReceiver multiChannelMidiReceiver, ToggleButton btn, Long fromTick) {
        if (AppInfo.INSTANCE.getSelectedDevice() == null) {
            DialogFactory.getInstance().renderWarningDialog("No device selected");
        } else {
            MidiDevice inputDevice = AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();
            if (inputDevice == null) {
                DialogFactory.getInstance().renderWarningDialog("Input device not ready");
                return false;
            } else {
                MidiPlayer.playMidi(successHandler, interruptedHandler, multiChannelMidiReceiver, btn, fromTick);

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

    @Getter
    private List<MidiEventInfo> markers = new ArrayList<>();

    public List<MidiEventInfo> extractMarkers() {
        File midi = AppInfo.INSTANCE.getMidiToPlay();
        markers = new ArrayList<>();

        if (midi != null) {
            try {
                Sequence sequence = MidiSystem.getSequence(midi);

                for (Track track : sequence.getTracks()) {
                    for (int i = 0; i < track.size(); i++) {
                        MidiEvent event = track.get(i);
                        MidiMessage message = event.getMessage();
                        long tick = MBTCalculator.convertTick(event.getTick());
                        MidiEventInfo midiEventInfo = new MidiEventInfo(tick, MBTCalculator.calculateMBT(tick), 0, event);

                        if (message instanceof MetaMessage metaMessage) {
                            midiEventInfo.setRootEventKey(META);

                            boolean isProcessed = metaProcessor.processMessage(metaMessage, midiEventInfo);
                            if (isProcessed) {
//                                if (midiEventInfo.getEventEventKey().equals(META_MARKER) || midiEventInfo.getEventEventKey().equals(META_TEMPO)) {
                                if (midiEventInfo.getEventEventKey().equals(META_MARKER)) {
                                    midiEventInfo.setName(midiEventInfo.getComment());
                                    markers.add(midiEventInfo);
                                }
                            }
                        }
                    }
                }
            } catch (InvalidMidiDataException | IOException e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }
        }

        return markers;
    }
}
