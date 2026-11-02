package entity.midi;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiSequence {
    /**
     * Is the file type
     * - type 0 (single-track)
     * - type 1 (multi-track)
     * - type 2 (pattern/song sequence)
     *
     * We only treat type 0 and type 1
     */
    @Getter
    @Setter
    private int fileType;

    /**
     * Pulses Per Quarter note (sometimes also PPQN)
     * By a tempo of 120 BPM (Beats Per Minute) the duration of a quarter note is exactly 500 milliseconds (60.000 ms / 120).
     * • By 96 PPQ one tick takes: (500 / 96 = 5,21 ms).
     * • By 960 PPQ one tick takes: (500 / 960 = 0,52 ms).
     */
    @Getter
    @Setter
    private int ppq;

    /**
     * Number of tracks
     */
    @Getter
    @Setter
    private int trackCount;

    /**
     * The tracks with the MidiFileEvents
     */
    @Getter
    private final Map<Integer, List<MidiFileEventInfo>> tracks;

    public MidiSequence() {
        this.fileType = -1;
        this.ppq = 480;
        this.trackCount = 0;
        this.tracks = new HashMap<>();
    }

    /**
     * Determine the Bpm throught the Meta Event (0xFF) type 0x51
     * @param sequence (is the sequence defined here)
     * @return the Beats Per Minute
     */
    public double getMidiBpm(MidiSequence sequence) {
        for (List<MidiFileEventInfo> trackEvents : sequence.getTracks().values()) {
            for (MidiFileEventInfo event : trackEvents) {
                // Check on a 3-byte data of the tempo meta event (0x51)
                if (event.status == 0xFF && event.data != null && event.data.length == 3) {
                    // MPQN = microseconds per quarter note
                    long mpqn = ((event.data[0] & 0xFF) << 16)
                            | ((event.data[1] & 0xFF) << 8)
                            | (event.data[2] & 0xFF);

                    if (mpqn > 0) {
                        return 60_000_000.0 / mpqn; // Convert MPQN to BPM
                    }
                }
            }
        }

        // If not found handle a default of 120 BPM
        return 120.0;
    }

    /**
     * Get the number of tocks in the file
     *
     * @return the ticks
     */
    public long getTickLength() {
        long maxTicks = 0;

        // Run through all tracks
        for (List<MidiFileEventInfo> trackEvents : tracks.values()) {
            if (!trackEvents.isEmpty()) {
                MidiFileEventInfo lastEvent = trackEvents.get(trackEvents.size() - 1);

                if (lastEvent.tick > maxTicks) {
                    maxTicks = lastEvent.tick;
                }
            }
        }

        return maxTicks;
    }
}
