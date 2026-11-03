package util.midi;

import entity.midi.MbtPosition;
import entity.midi.TimeSignature;
import lombok.Getter;

/**
 * The MBT (Measure - Beat - Tick) calculator is used to calculate MBT from ticks
 */
public class MBTCalculator {
    private static int ppq = 480;
    private static int beatsPerMeasure = 4;     // Upper position of Time Signature
    private static int beatValue = 4;           // Lower position of Time Signature

    @Getter
    private static int targetPPQ = 480;

    private static double ppqScalingFactor = 1.0;

    public static void setPPQ(int ppq) {
        MBTCalculator.ppq = ppq;
        ppqScalingFactor = (double)targetPPQ / ppq;
    }

    public static void setTimeSignature(TimeSignature timeSignature) {
        MBTCalculator.beatsPerMeasure = timeSignature.getBeatsPerMeasure();
        MBTCalculator.beatValue = timeSignature.getBeatValue();
    }

    /**
     * convert tick to targetPPQ
     * was orginal Math.round(tick * ppqScalingFactor)
     */
    public static long convertTick(long tick) {
        return tick;
    }

    /**
     * Calculate the MBT position from tick
     *
     * @param absoluteTick: the tick og the event
     *
     * @return the MBTPosition (with M, B and T set)
     */
    public static MbtPosition calculateMBT(long absoluteTick) {
        // 1. Scale the absolute tick dynamically based on PPQ-ratio
        long correctedTick = (long) (absoluteTick * ppqScalingFactor);

        // 2. Calculate how many ticks there go in one (beat)
        // By /4 measure = targetPPQ. By /8 measure = targetPPQ / 2
        long ticksPerBeat = (targetPPQ * 4L) / beatValue;

        // 3. How many ticks go in one complete measure?
        long ticksPerMeasure = ticksPerBeat * beatsPerMeasure;

        // 4. Calculate the measure (based on calculation done in 1)
        long measure = (correctedTick / ticksPerMeasure) + 1;
        long remainder = correctedTick % ticksPerMeasure;

        // 5. Calculate the beat inside this measure (based on calculation done in 1)
        long beat = (remainder / ticksPerBeat) + 1;

        // 6. Calculate the remaining ticks inside this beat
        long tick = remainder % ticksPerBeat;

        // 7. And this is the MBT position
        return new MbtPosition(measure, beat, tick);
    }

    /**
     * convert ticks to seconds (is used for e.g. duration calculation)
     *
     * @param ticks: the ticks the event took
     * @return number of seconds
     */
    public static double convertToSeconds(long ticks) {
        double secondsPerTick1 = 60.0 / (MidiFileHelper.getBPM() * targetPPQ);

        return ticks * secondsPerTick1;
    }
}
