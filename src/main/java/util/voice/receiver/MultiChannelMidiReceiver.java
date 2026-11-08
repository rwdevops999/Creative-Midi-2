package util.voice.receiver;

import entity.midi.Midi;
import entity.midi.MidiEventInfo;
import fx.component.ChannelIndicator;
import javafx.application.Platform;
import lombok.Setter;
import util.midi.MidiHelper;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Map;

import static util.Constants.CHANNEL_STYLE;

public class MultiChannelMidiReceiver implements Receiver {
    private MidiHelper midiHelper = MidiHelper.getInstance();

    private Map<Integer, ChannelIndicator> indicators;

    private boolean[] indicatorStatus = new boolean[16];

    @Setter
    private Receiver keyboardReceiver;

    public MultiChannelMidiReceiver() {
        super();
    }

    public MultiChannelMidiReceiver(Map<Integer, ChannelIndicator> channelIndicators) {
        this();

        indicators = channelIndicators;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (!(message instanceof ShortMessage sm)) {
            return;
        }

        int channel = sm.getChannel();
        int command = sm.getCommand();

        if (channel > 2 && channel < 9) {
            return;
        }

        int targetIndicatorChannel = (channel >= 9 && channel <= 16) ? CHANNEL_STYLE : channel;

        boolean listen = true;
        if (channel >= 0 && channel < 3) {
            var listenProp = midiHelper.getListen().get(channel);
            if (listenProp != null && !listenProp.get()) {
                listen = false;
            }
        } else if (channel >= 9 && channel <= 16) {
            var listenProp = midiHelper.getListen().get(CHANNEL_STYLE);
            if (listenProp != null && !listenProp.get()) {
                listen = false;
            }
        }

        if (!listen) {
            midiHelper.soundOff(channel);
            updateUIIndicator(targetIndicatorChannel, false);
            return;
        }

        if (keyboardReceiver != null) {
            keyboardReceiver.send(message, timeStamp);
        }

        if (command == ShortMessage.NOTE_ON) {
            int velocity = sm.getData2();
            if (velocity > 0) {
                updateUIIndicator(targetIndicatorChannel, true);
            } else {
                updateUIIndicator(targetIndicatorChannel, false);
            }
        } else if (command == ShortMessage.NOTE_OFF) {
            updateUIIndicator(targetIndicatorChannel, false);
        }
    }

    private void updateUIIndicator(int targetChannel, boolean turnOn) {
        if (indicators != null && indicators.containsKey(targetChannel)) {
            // Alleen actie ondernemen als de gewenste status verschilt van de huidige status
            if (indicatorStatus[targetChannel] != turnOn) {
                indicatorStatus[targetChannel] = turnOn;
                Platform.runLater(() -> {
                    ChannelIndicator indicator = indicators.get(targetChannel);
                    if (indicator != null) {
                        indicator.setActive(turnOn);
                    }
                });
            }
        }
    }

    @Override
    public void close() {}
}