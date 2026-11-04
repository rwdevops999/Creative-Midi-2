package util.midi;

import entity.midi.MidiEventInfo;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.midi.eventprocessors.MetaProcessor;
import util.midi.eventprocessors.MidiProcessor;
import util.midi.eventprocessors.SysexProcessor;

import javax.sound.midi.*;
import java.util.LinkedList;
import java.util.Queue;

import static util.midi.EventKeyType.*;

// TODO Complete this
public class MidiFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MidiFileProcessor.class);

    /**
     * Get instances of the 3 event processors
     */
    private static MidiProcessor midiProcessor = MidiProcessor.getInstance();
    private static MetaProcessor metaProcessor = MetaProcessor.getInstance();
    private static SysexProcessor sysexProcessor = SysexProcessor.getInstance();

    @Getter
    private static Queue<MidiEventInfo> events = new LinkedList<>();

    /**
     * Process MIDI events in standard sequence
     *
     * @param sequence
     */
    public static void processMidi(Sequence sequence) {
        /**
         * Create a new queue (i.e. a linked list) of events
         */
        events = new LinkedList<>();

        // Track doesn't keep the track number, so we do it ourselves
        int trackNumber = 0;

        // we run through each track
        for (Track track : sequence.getTracks()) {
            trackNumber++;

            // process all events in this track
            for (int i = 0; i < track.size(); i++) {
                /**
                 * We get a notification if the event is processed.
                 * Some events we don't want in the list (such as e.g. active sensing)
                 * if isProcessed == true we add it to the list
                 */
                boolean isProcessed = false;

                /**
                 * get the event from the track
                 */
                MidiEvent event = track.get(i);

                /**
                 * get the message of the event (=> from here we know of what the
                 * event is about)
                 */
                MidiMessage message = event.getMessage();

                /**
                 * Convert the tick to target PPQ
                 */
                long tick = MBTCalculator.convertTick(event.getTick());
                MidiEventInfo midiEventInfo = new MidiEventInfo(tick, MBTCalculator.calculateMBT(tick), trackNumber, event);

                /**
                 * Here we are going to process the events by type
                 */
                if (message instanceof ShortMessage midiMessage) {
                    isProcessed = midiProcessor.processMessage(midiMessage, midiEventInfo);
                } else if (message instanceof MetaMessage metaMessage) {
                    midiEventInfo.setRootEventKey(META);

                    isProcessed = metaProcessor.processMessage(metaMessage, midiEventInfo);
                } else if (message instanceof SysexMessage sysexMessage) {
                    // It is a SYSEX event
                    midiEventInfo.setRootEventKey(SYSEX);

//                    isProcessed = SysexProcessor.processMessage(sysexMessage, midiEventInfo);
                }  else {
                    // Unknown event
                    logger.error("Error. Unknown event message type: {}", message.getClass().getName());
                }

                if (isProcessed) {
                    events.add(midiEventInfo);
                }
            }
        }
    }
}
