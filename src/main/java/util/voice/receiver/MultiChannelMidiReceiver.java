package util.voice.receiver;

import fx.component.ChannelIndicator;
import javafx.application.Platform;
import lombok.Setter;
import util.midi.MidiHelper;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Map;

public class MultiChannelMidiReceiver implements Receiver {
    private MidiHelper midiHelper = MidiHelper.getInstance();

    private Map<Integer, ChannelIndicator> indicators;

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
        if (message instanceof ShortMessage sm) {
            int channel = sm.getChannel();

            // 1. Thread-safe null/bounds check to avoid NullPointerException
            if ((channel >= 0 && channel < 4)) {
                if (midiHelper.getListen().get(channel) != null && (!midiHelper.getListen().get(channel).get())) {
                    if (indicators != null && indicators.containsKey(channel)) {
                        Platform.runLater(() -> {
                            ChannelIndicator indicator = indicators.get(channel);
                            if (indicator != null) {
                                indicator.setActive(false);
                            }
                        });
                    }

                    return;
                }
            }

            if (keyboardReceiver != null) {
                keyboardReceiver.send(message, timeStamp);
            }

            int command = sm.getCommand();

            // 2. Bound checking matching your indicator layout
            if (indicators != null && indicators.containsKey(channel)) {
                boolean turnOn;

                if (command == ShortMessage.NOTE_ON) {
                    turnOn = (sm.getData2() > 0);
                } else if (command == ShortMessage.NOTE_OFF) {
                    turnOn = false;
                } else {
                    return;
                }

                Platform.runLater(() -> {
                    ChannelIndicator indicator = indicators.get(channel);
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