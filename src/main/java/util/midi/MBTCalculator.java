package util.midi;

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
}
