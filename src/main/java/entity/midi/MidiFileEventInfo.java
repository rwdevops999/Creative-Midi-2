package entity.midi;

import lombok.Getter;
import lombok.Setter;

/**
 * Information of an event inside a midi file
 */
@Getter
@Setter
public class MidiFileEventInfo {
    /**
     * The added ritme in ticks from the beginning
     */
    public long tick;

    /**
     * The Event type (e.g. 0xFF for meta)
     */
    public int status;

    /**
     * The raw data of the event
     */
    public byte[] data;

    /**
     * byte defining the kind of meta message (0x00 through 0x7F)
     */
    public int metaType;

    /**
     * The track id
     */
    private int track;

    public MidiFileEventInfo(long tick, int status, byte[] data, int metaType) {
        this.tick = tick;
        this.status = status;
        this.data = data;
        this.metaType = metaType;
    }

    public MidiFileEventInfo(long tick, int status, byte[] data) {
        this(tick, status, data, -1);
    }
}
