package util.midi;

import entity.midi.MidiFileEventInfo;
import entity.midi.MidiSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.List;
import java.util.Map;

/**
 * Convert the custom sequence to a standard one
 */
public class MidiSequenceConverter {
    private static final Logger logger = LoggerFactory.getLogger(MidiSequenceConverter.class);

    public static Sequence convertToStandardSequence(MidiSequence customSeq) throws Exception {
        // Create a new empty sequence with PPQ filled in.
        Sequence standardSequence = new Sequence(Sequence.PPQ, customSeq.getPpq());

        // Process all the tracks
        for (Map.Entry<Integer, List<MidiFileEventInfo>> entry : customSeq.getTracks().entrySet()) {
            // get all (custom) events on this track (this is coming from midi parser)
            List<MidiFileEventInfo> customEvents = entry.getValue();

            // create an empty standard track (!!! standardTrack is a reference to this new track)
            Track standardTrack = standardSequence.createTrack();

            // put all events into this track
            for (MidiFileEventInfo customEvent : customEvents) {
                // create a midi message
                MidiMessage message = createMidiMessage(customEvent);

                if (message != null) {
                    // add the official event to the track
                    MidiEvent standardEvent = new MidiEvent(message, customEvent.tick);
                    standardTrack.add(standardEvent);
                }
            }

        }

        return standardSequence;
    }

    // Helper method for converting event (raw status and byte-arrays) to javax.sound.midi objects
    private static MidiMessage createMidiMessage(MidiFileEventInfo customEvent) {
        try {
            int status = customEvent.status;
            byte[] data = customEvent.data != null ? customEvent.data : new byte[0];

            // Process Meta events
            if (status == 0xFF) {
                MetaMessage metaMessage = new MetaMessage();
                metaMessage.setMessage(
                        customEvent.getMetaType(),
                        customEvent.getData(),
                        customEvent.getData().length
                );
                return metaMessage;
            }

            // Processs Sysex events
            if (status == 0xF0 || status == 0xF7) {
                SysexMessage sysex = new SysexMessage();
                sysex.setMessage(status, data, data.length);
                return sysex;
            }

            // Process Midi events
            if (status >= 0x80 && status < 0xF0) {
                ShortMessage shortMsg = new ShortMessage();
                int data1 = (data.length > 0) ? (data[0] & 0xFF) : 0;
                int data2 = (data.length > 1) ? (data[1] & 0xFF) : 0;

                shortMsg.setMessage(status, data1, data2);
                return shortMsg;
            }

        } catch (InvalidMidiDataException imde) {
            logger.error("Exception: CAUSE {}", imde.getMessage());
        }

        logger.error("Error: event can't be processed");

        return null;
    }
}
