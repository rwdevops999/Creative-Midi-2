package util.voice.receiver;

import entity.midi.Midi;
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

                                Midi midi = null;
                                try {
                                    System.out.println("ALL SOUND OFF on " + channel);
                                    midi = new Midi("All Sound Off", channel, 0xB0, 0x78, 0x00);
                                    midiHelper.sendMidi(midi);
                                } catch (InvalidMidiDataException e) {
                                }
                            }
                        });
                    }

                    return;
                }
            } else if (channel >= 9) {
                midiHelper.getListen().get(CHANNEL_STYLE);
                if (midiHelper.getListen().get(CHANNEL_STYLE) != null && (!midiHelper.getListen().get(CHANNEL_STYLE).get())) {
                    Platform.runLater(() -> {
                        ChannelIndicator indicator = indicators.get(CHANNEL_STYLE);
                        if (indicator != null) {
                            indicator.setActive(false);
                        }
                    });

                    return;
                }
            }

            if (keyboardReceiver != null) {
                keyboardReceiver.send(message, timeStamp);
            }

            int command = sm.getCommand();

            // 2. Bound checking matching your indicator layout
            if (indicators != null && (indicators.containsKey(channel) || (channel >= 9 && indicators.containsKey(CHANNEL_STYLE)))) {
                boolean turnOn;

                if (command == ShortMessage.NOTE_ON) {
                    turnOn = (sm.getData2() > 0);
                } else if (command == ShortMessage.NOTE_OFF) {
                    turnOn = false;
                } else {
                    return;
                }

                Platform.runLater(() -> {
                    if (channel >= 0 && channel < 4) {
                        ChannelIndicator indicator = indicators.get(channel);
                        if (indicator != null) {
                            indicator.setActive(turnOn);
                        }
                    } else if (channel >= 9) {
                            ChannelIndicator indicator = indicators.get(CHANNEL_STYLE);
                            if (indicator != null) {
                                indicator.setActive(turnOn);
                            }
                        }
                });
            }
        }
    }

    @Override
    public void close() {}
}