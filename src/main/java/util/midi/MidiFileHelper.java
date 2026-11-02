package util.midi;

import entity.midi.TimeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.time.Duration;

import static util.Constants.TEMPO_EVENT;
import static util.Constants.TIME_SIGNATURE_EVENT;

/**
 * Helper class to calculate stuff for UI
 */
public class MidiFileHelper {
    private static final Logger logger = LoggerFactory.getLogger(MidiFileHelper.class);

    private static int ppq = 480;
    private static int bpm = 120;
    private static TimeSignature timeSignature = new TimeSignature(4,4);
    private static String duration;

    public static void setPPQ(int value) {
        MidiFileHelper.ppq = value;
    }

    public static int getPPQ() {
        return MidiFileHelper.ppq;
    }

    public static int getBPM() {
        return MidiFileHelper.bpm;
    }

    public static TimeSignature getTimeSignature() {
        return MidiFileHelper.timeSignature;
    }

    public static String getDuration() {
        return MidiFileHelper.duration;
    }

    public static void retrieveBpmAndTimeSignature(Sequence sequence) {
        int foundValue = 0;
        // calculate default duration
        double seconds = (double) (sequence.getTickLength() * 60) / (bpm * MBTCalculator.getTargetPPQ());
        Duration durationFromSecs = Duration.ofMillis((long) (seconds * 1000));

        MidiFileHelper.duration = formatDuration(durationFromSecs);

        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);

                MidiMessage message = event.getMessage();

                if (message instanceof MetaMessage metaMessage) {
                    if (metaMessage.getType() == TEMPO_EVENT) {
                        MidiFileHelper.bpm = processTempo(metaMessage);

                        seconds = (double) (sequence.getTickLength() * 60) / (bpm * MidiFileHelper.getPPQ());
                        durationFromSecs = Duration.ofMillis((long) (seconds * 1000));

                        MidiFileHelper.duration = formatDuration(durationFromSecs);
                        foundValue++;
                        if (foundValue == 2) {
                            break;
                        }

                    } else if (metaMessage.getType() == TIME_SIGNATURE_EVENT) {
                        byte[] data = metaMessage.getData();

                        if (data.length >= 2) {
                            // 1. The numerator is straightforward
                            int numerator = data[0] & 0xFF;

                            // 2. The denominator is stored as a power of 2 (2^n)
                            int denominatorPower = data[1] & 0xFF;
                            int denominator = (int) Math.pow(2, denominatorPower);

                            MidiFileHelper.timeSignature = new TimeSignature(numerator, denominator);

                            foundValue++;
                            if (foundValue == 2) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static int processTempo(MetaMessage metaMessage) {
        double bpm = 0.0f;
        byte[] data = metaMessage.getData();

        if (data.length == 3) {
            // Reconstruct the microseconds per quarter note from the 3 bytes
            int mpqn = ((data[0] & 0xFF) << 16)
                    | ((data[1] & 0xFF) << 8)
                    | (data[2] & 0xFF);

            // Calculate BPM
            bpm = Math.floor(60_000_000f / mpqn);
        }

        return (int)bpm;
    }

    private static String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                duration.toMillisPart()
        );
    }
}
