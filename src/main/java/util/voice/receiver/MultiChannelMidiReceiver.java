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

    private boolean[] channelMuted = new boolean[16];
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

    private Integer[] notesOn = new Integer[16];

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (!(message instanceof ShortMessage sm)) {
            return;
        }

        int channel = sm.getChannel();
        int command = sm.getCommand();

        if (channel >= 3 && channel <= 8) {
            return;
        }

        // 1. HARD ANTI-LOOP FILTER
        if (command == ShortMessage.CONTROL_CHANGE) {
            int ccNumber = sm.getData1();
            if (ccNumber == 120 || ccNumber == 123 || ccNumber == 121) {
                return;
            }
        }

        int targetIndicatorChannel = (channel >= 9 && channel <= 16) ? CHANNEL_STYLE : channel;

        // Check of we moeten luisteren naar dit kanaal
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

        // --- DE DEUR-DICHT BLOKKADE (VEILIG & EENMALIG) ---
        // --- DE DEUR-DICHT BLOKKADE (HARDWARE PANIC RESET) ---
        if (!listen) {
            // Zodra listen op false springt, voeren we EENMALIG een harde reset uit
            if (!channelMuted[channel]) {
                channelMuted[channel] = true; // Zet direct de vlag om hangen te voorkomen

                if (keyboardReceiver != null) {
                    try {
                        // We vegen NIET alleen dit kanaal schoon, maar sturen een gerichte reset
                        // naar de live-kanalen (0,1,2) en stijl-kanalen (9..16) om de Yamaha te dwingen alles te lossen.
                        int[] kanalenTeResetten = {0, 1, 2, 9, 10, 11, 12, 13, 14, 15, 16};

                        for (int ch : kanalenTeResetten) {
                            // 1. Schakel sustainpedaal (CC 64) direct uit voor dit kanaal
                            keyboardReceiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, ch, 64, 0), -1);

                            // 2. Stuur 128 Note-Offs om alle hangende toetsen/arranger-triggers los te laten
                            for (int note = 0; note < 128; note++) {
                                keyboardReceiver.send(new ShortMessage(ShortMessage.NOTE_OFF, ch, note, 0), -1);
                            }
                        }
                    } catch (Exception e) {
                        // Stille catch
                    }
                }

                // UI eenmalig updaten naar 'uit'
                if (indicators != null && indicators.containsKey(targetIndicatorChannel)) {
                    if (indicatorStatus[targetIndicatorChannel]) {
                        indicatorStatus[targetIndicatorChannel] = false;
                        Platform.runLater(() -> {
                            ChannelIndicator indicator = indicators.get(targetIndicatorChannel);
                            if (indicator != null) indicator.setActive(false);
                        });
                    }
                }
            }

            // Blokkeer alle nieuwe binnenkomende buffer-berichten van de sequencer voor dit kanaal
            return;
        }
        
// --- DE CODE ALS LISTEN TRUE IS (KANAAL MOET AFSPELEN) ---

// Als dit kanaal zojuist gedempt was, herstel EXPRESSION (CC 11) eenmalig naar vol volume (127)
        if (channelMuted[channel]) {
            channelMuted[channel] = false; // Reset de mute vlag
            if (keyboardReceiver != null) {
                try {
                    ShortMessage expressionRestore = new ShortMessage();
                    expressionRestore.setMessage(ShortMessage.CONTROL_CHANGE, channel, 11, 127); // CC 11 terug naar 127
                    keyboardReceiver.send(expressionRestore, -1);
                } catch (Exception e) {}
            }
        }

        // Stuur het bericht normaal door
        if (keyboardReceiver != null) {
            keyboardReceiver.send(message, timeStamp);
        }

        // Noot-tracker en UI-updates
        if (command == ShortMessage.NOTE_ON) {
            int velocity = sm.getData2();
            if (velocity > 0) {
                notesOn[channel] = sm.getData1();
                updateUIIndicator(targetIndicatorChannel, true);
            } else {
                notesOn[channel] = -1;
                updateUIIndicator(targetIndicatorChannel, false);
            }
        } else if (command == ShortMessage.NOTE_OFF) {
            notesOn[channel] = -1;
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