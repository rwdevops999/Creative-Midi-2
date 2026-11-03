package entity.midi;

import entity.NameEntity;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import util.midi.EventKeyType;

import javax.sound.midi.MidiEvent;
import javax.swing.text.Position;

public class MidiEventInfo extends NameEntity {
    /**
     * The type event (MIDI, META or SYSEX)
     */
    @Getter
    @Setter
    private EventKeyType rootEventKey;

    /**
     * The specific event (TEMPO, BANK SELECT MSB, ...)
     */
    @Getter
    @Setter
    private EventKeyType eventEventKey;

    /**
     * The event tick (converted to target PPQ)
     */
    @Getter
    @Setter
    private long tick;

    /**
     * The MBT value
     */
    @Getter
    @Setter
    private String mbtPosition;

    /**
     * The id of the track the event belongs to
     */
    @Getter
    @Setter
    private int trackId;

    /**
     * The Original event
     */
    @Getter
    @Setter
    private MidiEvent originalEvent;

    public MidiEventInfo(long tick, MbtPosition mbtPosition, int trackId, MidiEvent originalEvent) {
        this.tick = tick;
        this.mbtPosition = mbtPosition.toString();
        this.trackId = trackId;
        this.originalEvent = originalEvent;
    }

    /**
     * The color of the event (converted to long)
     */
    @Setter
    @Getter
    private long colorAsLong;

    public void setColor(Color color) {
        this.colorAsLong = colorToLong(color);
    }

    /**
     * Color to Long = RRGGBBAA
     * @param color
     * @return
     */
    private long colorToLong(Color color) {

        // Convert the 0.0-1.0 double values from the color to 0-255 integers
        long r = Math.round(color.getRed() * 255);
        long g = Math.round(color.getGreen() * 255);
        long b = Math.round(color.getBlue() * 255);
        long a = Math.round(color.getOpacity() * 255);

        // Shift bits to pack them into a single value (RGBA format)
        return ((r << 24) | (g << 16) | (b << 8) | a)  & 0xFFFFFFFFL;
    }

    /**
     * The duration of the event (especially Note On events)
     */
    @Setter
    @Getter
    private String duration = null;

    /**
     * The description of the event (especially Note On events)
     */
    @Setter
    @Getter
    private String description;

    /**
     * Additional description (can contain strings or integers)
     */
    @Setter
    @Getter
    private String comment;  // this can contain integers or strings

    /**
     * a value
     */
    @Setter
    @Getter
    private Integer value = null;

    /**
     * the channel if necessary
     */
    @Setter
    @Getter
    private Integer channel = null;

    /**
     * the hex message
     */
    @Setter
    @Getter
    private String message;
}
