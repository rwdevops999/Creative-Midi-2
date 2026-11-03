package util.midi;

import entity.midi.Midi;
import entity.midi.MidiEventInfo;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.ShortMessage;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MidiNoteMatcher {
    private static final Logger logger = LoggerFactory.getLogger(MidiNoteMatcher.class);

    @Getter
    private static final MidiNoteMatcher instance = new MidiNoteMatcher();

    private MidiNoteMatcher() {
    }

    private record NoteKey(int channel, int noteNumber) {}
    private final Map<NoteKey, Queue<MidiEventInfo>> activeNotes = new HashMap<>();

    /**
     * Here we process the Note On and Note Off events
     * - note on events are just store
     * - note off events search for matching note on event
     *
     * @param message
     * @param midiEventInfo
     * @return
     */
    public boolean processEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean isNoteOn = (message.getStatus() & 0xF0) == 0x90;
        boolean isNoteOff = (message.getStatus() & 0xF0) == 0x80;

        NoteKey key = new NoteKey(message.getChannel(), message.getData1());

        if (isNoteOn && message.getData2() > 0) {
            // This is a note on with some velocity
            activeNotes.computeIfAbsent(key, k -> new LinkedList<>()).add(midiEventInfo);
        } else if (isNoteOff || (isNoteOn && message.getData2() == 0)) {
            // This is a note off OR a note on with velocity 0 (which is also
            // seen as a note off
            Queue<MidiEventInfo> onsetQueue = activeNotes.get(key);
            if (onsetQueue != null && !onsetQueue.isEmpty()) {
                MidiEventInfo noteOnEvent = onsetQueue.poll();

                handleMatchingNoteFound(noteOnEvent, midiEventInfo);

                if (onsetQueue.isEmpty()) {
                    activeNotes.remove(key);
                }

                copyEvents(noteOnEvent, midiEventInfo);
                return true;
            }
        }

        return false;
    }

    /**
     * For a note off event we have found the matching note on event
     * - calculate and set the duration in the note on event
     */

    private void handleMatchingNoteFound(MidiEventInfo noteOn, MidiEventInfo noteOff) {
        long durationTicks = noteOff.getOriginalEvent().getTick() - noteOn.getOriginalEvent().getTick();
        double seconds = MBTCalculator.convertToSeconds(durationTicks);
        noteOn.setDuration(createDurationString(seconds));
    }

    /**
     * From seconds create a duration string
     *
     * @param seconds: the number of seconds the event took
     *
     * @return "HH:MM:SS.ms" string
     */
    private static String createDurationString(double seconds) {
        Duration duration = Duration.ofNanos((long) (seconds * 1_000_000_000));

        long lHours = duration.toHours();
        long lMinutes = duration.toMinutesPart();
        long lSeconds = duration.toSecondsPart();

        // Extract milliseconds and scale down to 2 digits (.mm)
        long millis2Digits = duration.toMillisPart() / 10;

        return String.format("%02d:%02d:%02d.%02d", lHours, lMinutes, lSeconds, millis2Digits);
    }

    /**
     * Copy Event information from src to dst
     *
     * @param src
     * @param dst
     */
    private void copyEvents(MidiEventInfo src, MidiEventInfo dst) {
        dst.setTick(src.getTick());
        dst.setMbtPosition(src.getMbtPosition());
        dst.setOriginalEvent(src.getOriginalEvent());
        dst.setValue(src.getValue());
        dst.setMessage(src.getMessage());
        dst.setDuration(src.getDuration());
    }
}
