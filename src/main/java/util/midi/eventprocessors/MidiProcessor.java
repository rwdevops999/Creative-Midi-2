package util.midi.eventprocessors;

import entity.midi.MidiEventInfo;
import javafx.scene.paint.Color;
import lombok.Getter;
import util.SysexToHexStringConvertor;
import util.app.colors.ColorScheme;
import util.midi.EventKeyType;
import util.midi.MidiNoteMatcher;
import util.properties.AppProperties;

import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;

import static util.Util.calcNote;
import static util.app.colors.ColorScheme.getColor;
import static util.midi.EventKeyType.MIDI_NOTE_OFF;
import static util.midi.EventKeyType.MIDI_PROGRAM_CHANGE;
import static util.properties.PropertyKeys.BASE_OCTAVE;

public class MidiProcessor {
    @Getter
    private static MidiProcessor instance = new MidiProcessor();

    private MidiProcessor() {
    }

    /**
     * These are colors specific to midi events (they are added to the colorScheme)
     * - midi events are normally BLUE except the ones that are unknown/invalid which are RED
     *
     */
    private static final Map<ColorScheme.ColorKey, Color> colors = new HashMap<>();

    static {
        // Global Color
        colors.put(new ColorScheme.ColorKey("midi"), Color.BLUE);

        // Event Colors
        colors.put(new ColorScheme.ColorKey("midi", "program change"), Color.GOLD);

        // Problem colors
        colors.put(new ColorScheme.ColorKey("midi", "unknown or error"), Color.RED);

        ColorScheme.addColorsToScheme(colors);
    }

    public boolean processMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        /**
         * All these message are MIDI messages
         */

        midiEventInfo.setRootEventKey(EventKeyType.MIDI);

        midiEventInfo.setColor(getColor("midi"));
        midiEventInfo.setMessage(SysexToHexStringConvertor.convertToHexString(message.getMessage()));

        int statusBytes = message.getStatus() & 0xF0;

        switch (statusBytes) {
            case
                0x80,
                0x90,
                0xA0,
                0xB0,
                0xC0,
                0xD0,
                0xE0 -> processChannelMessage(message, midiEventInfo);
            case
                0xF0 -> processRealtimeMessage(message, midiEventInfo);
            default -> processUnknowMessage(message, midiEventInfo);
        }
        return result;
    }

    /**
     * Processing of Channel Messages
     */
    private static boolean processChannelMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        // get the raw bytes
        byte[] rawBytes = message.getMessage();

        /**
         * this is the command message (is actually the same as status byte without channel information)
         */
        int command = rawBytes[0] & 0xF0;

        result = switch (command) {
            case 0x80 -> handleNoteOffEvent(message, midiEventInfo);
            case 0x90 -> handleNoteOnEvent(message, midiEventInfo);
            case 0xB0 -> handleControlChangeOrModeMessageEvent(message, midiEventInfo);
            case 0xC0 -> handleProgramChangeEvent(message, midiEventInfo);
            case 0xD0 -> handleChannelAftertouchEvent(message, midiEventInfo);
            case 0xA0 -> handlePolyphonicAftertouchEvent(message, midiEventInfo);
            case 0xE0 -> handlePitchBendChangeEvent(message, midiEventInfo);
            default -> handleInvalidChannelMessage(message, midiEventInfo);
        };

        return result;
    }

    private static final MidiNoteMatcher midiNoteMatcher = MidiNoteMatcher.getInstance();

    private static boolean handleNoteOffEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_NOTE_OFF);

        /**
         * We give this event a note color (this is used to close a note on,
         * so the note matching note on will be returned)
         */
        midiEventInfo.setColor(getColor("midi", "note"));
        midiEventInfo.setChannel(message.getChannel()+1);

        midiEventInfo.setDescription("Note");

        int data1 = message.getData1(); // is the note key
        int data2 = message.getData2(); // is the velocity

        /*
         * The note ('C#', ...) is displayed in the comment
         */
        midiEventInfo.setComment(calcNote(data1).getName());

        /*
         * The velocity comes in the value
         */
        midiEventInfo.setValue(data2);

        /*
         * look for matching note on
         */
        result = midiNoteMatcher.processEvent(message, midiEventInfo);

        return result;
    }

    private static boolean handleNoteOnEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_NOTE_OFF);

        /*
         * We give this event a note color (this is used to close a note on,
         * so the note matching note on will be returned)
         */
        midiEventInfo.setColor(getColor("midi", "note"));

        /*
         * Internally the channel is 0 based (0..15), for UI reasons
         * it is converted to 1-based (1 - 16)
         */
        midiEventInfo.setChannel(message.getChannel()+1);

        midiEventInfo.setDescription("Note");

        int data1 = message.getData1(); // is the note key
        int data2 = message.getData2(); // is the velocity

        /*
         * The note ('C#', ...) is displayed in the comment
         */
        midiEventInfo.setComment(calcNote(data1).getName());
        /*
         * The velocity comes in the value
         */
        midiEventInfo.setValue(data2);

        /*
         * If it is a note On with velocity > 0 -> Store it
         * If it is a note On with velocaity = 0 -> behaves like a note off
         */
        result = midiNoteMatcher.processEvent(message, midiEventInfo);

        return result;
    }

    private static boolean handleControlChangeOrModeMessageEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        byte[] rawBytes = message.getMessage();
        int byte1 = rawBytes[1] & 0xFF; // Tells us over what change it goes here

        result = switch (byte1) {
            case
                0x00,
                0x01,
                0x02,
                0x05,
                0x06,
                0x07,
                0x0A,
                0x0B,
                0x10,
                0x20,
                0x26,
                0x40,
                0x41,
                0x42,
                0x43,
                0x47,
                0x48,
                0x49,
                0x4A,
                0x4B,
                0x4C,
                0x4D,
                0x4E,
                0x50,
                0x51,
                0x52,
                0x54,
                0x5B,
                0x5D,
                0x5E,
                0x60,
                0x61,
                0x62,
                0x63,
                0x64,
                0x65 -> handleControlChangeMessage(message, midiEventInfo);
            case
                0x78,
                0x79,
                0x7A,
                0x7B,
                0x7C,
                0x7D,
                0x7E,
                0x7F -> handleModeChangeMessage(message, midiEventInfo);
            default -> handleUnknownControlOrModeMessage(message, midiEventInfo);

        };

        return result;
    }

    private static boolean handleProgramChangeEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_PROGRAM_CHANGE);

        /**
         * Program change get a color (because it is important)
         */
        midiEventInfo.setColor(getColor("midi", "program change"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Program change");

        byte[] rawBytes = message.getMessage();
        int byte1 = rawBytes[1] & 0xFF;

        midiEventInfo.setData2(byte1);

        int msb = getMSB(message.getChannel());
        int lsb = getLSB(message.getChannel());
        String voice = searchVoice(msb, lsb, byte1);

        midiEventInfo.setComment(voice);

        return result;
    }

    private static boolean handleChannelAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handlePolyphonicAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * Control Change Messages
     */
    private static boolean handleControlChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * Mode Change Messages
     */
    private static boolean handleModeChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * Processing of Real-time Messages
     */
    private static boolean handlePitchBendChangeEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * From here the problem cases
     */
    private static boolean processUnknowMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown midi event");

        return result;
    }

    private static boolean handleInvalidChannelMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown midi event");

        return result;
    }

    private static boolean handleUnknownControlOrModeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown midi event");

        return result;
    }
}
