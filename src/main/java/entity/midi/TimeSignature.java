package entity.midi;

import org.slf4j.helpers.MessageFormatter;

/**
 * The Time Signature consists of
 * - a numerator: (Top Number): Indicates how many beats are in each measure
 * - a denominator: (Bottom Number):  Indicates what kind of note.
 * e.g. 3/4 or 6/8
 */
public class TimeSignature {
    private int numerator;
    private int denominator;

    public TimeSignature(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public int getBeatValue() {
        return this.numerator;
    }

    public int getBeatsPerMeasure() {
        return this.denominator;
    }

    @Override
    public String toString() {
        return MessageFormatter.basicArrayFormat("{}/{}", new Object[]{numerator, denominator});
    }
}
