package entity.midi;

/**
 * The MBT Values
 */
public class MbtPosition {
    public final long measure;
    public final long beat;
    public final long tickWithinBeat;

    public MbtPosition(long measure, long beat, long tickWithinBeat) {
        this.measure = measure;
        this.beat = beat;
        this.tickWithinBeat = tickWithinBeat;
    }

    @Override
    public String toString() {
        return String.format("%03d:%1d:%04d",
                this.measure,
                this.beat,
                this.tickWithinBeat);
    }
}
