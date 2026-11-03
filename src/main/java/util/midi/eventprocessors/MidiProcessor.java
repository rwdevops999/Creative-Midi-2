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
import static util.midi.EventKeyType.*;
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

    private static final int[] currentMSB = new int[16];
    private static final int[] currentLSB = new int[16];

    static {
        // Global Color
        colors.put(new ColorScheme.ColorKey("midi"), Color.BLUE);

        // Event Colors
        colors.put(new ColorScheme.ColorKey("midi", "bank select msb"), Color.GOLD); // 1 of 3
        colors.put(new ColorScheme.ColorKey("midi", "bank select lsb"), Color.GOLD); // 2 of 3
        colors.put(new ColorScheme.ColorKey("midi", "program change"), Color.GOLD);  // 3 of 3
        colors.put(new ColorScheme.ColorKey("midi", "pitch bend"), Color.MEDIUMSPRINGGREEN);
        colors.put(new ColorScheme.ColorKey("midi", "main volume"), Color.LIGHTSKYBLUE);
        colors.put(new ColorScheme.ColorKey("midi", "panpot"), Color.DARKORANGE);
        colors.put(new ColorScheme.ColorKey("midi", "expression"), Color.DARKORCHID);

        // Problem colors
        colors.put(new ColorScheme.ColorKey("midi", "unknown or error"), Color.RED);

        ColorScheme.addColorsToScheme(colors);

        java.util.Arrays.fill(currentMSB, 0);
        java.util.Arrays.fill(currentLSB, 0);
    }

    /**
     * This is to find the matching MSB/LSB when a PC is received
     *
     * @param channel
     * @return
     */
    private static int getMSB(int channel) { return currentMSB[channel]; }
    private static void setMSB(int channel, int value) { currentMSB[channel] = value; }
    private static int getLSB(int channel) { return currentLSB[channel]; }
    private static void setLSB(int channel, int value) { currentLSB[channel] = value; }

    public boolean processMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        /**
         * All these message are MIDI messages
         */

        midiEventInfo.setRootEventKey(EventKeyType.MIDI);

        midiEventInfo.setColor(getColor("midi"));
        midiEventInfo.setMessage(SysexToHexStringConvertor.convertToHexString(message.getMessage()));

        int statusBytes = message.getStatus() & 0xF0;

        result = switch (statusBytes) {
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
        };

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

    /**
     * Here come the channel messages
     */
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
        int pc = rawBytes[1] & 0xFF;

        // voice number
        midiEventInfo.setValue(pc);

        /**
         * Lookup the matching MSB/LSB value which together with PC defines voice
         */
        int msb = getMSB(message.getChannel());
        int lsb = getLSB(message.getChannel());

        // TODO When voice files are processed
//        String voice = searchVoice(msb, lsb, pc);
//        midiEventInfo.setComment(voice);

        midiEventInfo.setComment("Voice Unknown MSB="+msb+", LSB="+lsb+", PC="+pc);

        return result;
    }

    private static boolean handleChannelAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_CHANNEL_AFTERTOUCH);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Channel aftertouch");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[1] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handlePolyphonicAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_KEY_AFTERTOUCH);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Key aftertouch");

        byte[] rawBytes = message.getMessage();
        int keyNo = rawBytes[1] & 0xFF;

        midiEventInfo.setComment(calcNote(keyNo).getName());

        return result;
    }

    /**
     * Control Change Control
     */
    private static boolean handleControlChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        byte[] rawBytes = message.getMessage();

        int byte1 = rawBytes[1] & 0xFF;

        result = switch (byte1) {
            case 0x00 -> handleBankSelectMSB(message, midiEventInfo);
            case 0x01 -> handleModulation(message, midiEventInfo);
            case 0x02 -> handleBreath(message, midiEventInfo);
            case 0x05 -> handlePortamentoTime(message, midiEventInfo);
            case 0x06 -> handleDataEntryMSB(message, midiEventInfo);
            case 0x07 -> handleMainVolume(message, midiEventInfo);
            case 0x0A -> handlePanPot(message, midiEventInfo);
            case 0x0B -> handleExpression(message, midiEventInfo);
            case 0x10 -> handleGeneralPurposeController(message, midiEventInfo);
            case 0x20 -> handleBankSelectLSB(message, midiEventInfo);
            case 0x26 -> handleDataEntryLSB(message, midiEventInfo);
            case 0x40 -> handleSustainDamper(message, midiEventInfo);
            case 0x41 -> handlePortamento(message, midiEventInfo);
            case 0x42 -> handleSostenuto(message, midiEventInfo);
            case 0x43 -> handleSoftPedal(message, midiEventInfo);
            case 0x47 -> handleResonance(message, midiEventInfo);
            case 0x48 -> handleReleaseTime(message, midiEventInfo);
            case 0x49 -> handleAttackTime(message, midiEventInfo);
            case 0x4A -> handleCutoff(message, midiEventInfo);
            case 0x4B -> handleDecayTime(message, midiEventInfo);
            case 0x4C -> handleVibratoRate(message, midiEventInfo);
            case 0x4D -> handleVibratoDepth(message, midiEventInfo);
            case 0x4E -> handleVibratoDelay(message, midiEventInfo);
            case 0x50 -> handleArticulation1(message, midiEventInfo);
            case 0x51 -> handleArticulation2(message, midiEventInfo);
            case 0x52 -> handleArticulation3(message, midiEventInfo);
            case 0x54 -> handlePortamentoControl(message, midiEventInfo);
            case 0x5B -> handleReverbSendLevel(message, midiEventInfo);
            case 0x5D -> handleChorusSendLevel(message, midiEventInfo);
            case 0x5E -> handleVariationSendLevel(message, midiEventInfo);
            case 0x60 -> handleRPNIncrement(message, midiEventInfo);
            case 0x61 -> handleRPNDecrement(message, midiEventInfo);
            case 0x62 -> handleNRPNLsb(message, midiEventInfo);
            case 0x63 -> handleNRPNMsb(message, midiEventInfo);
            case 0x64 -> handleRPNLsb(message, midiEventInfo);
            case 0x65 -> handleRPNMsb(message, midiEventInfo);
            default -> handleUnknownControlChangeMessage(message, midiEventInfo);
        };

        return result;
    }

    /**
     * Here comes the Control Change Messages
     */
    private static boolean handleBankSelectMSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_BANK_SELECT_MSB);

        midiEventInfo.setColor(getColor("midi", "bank select msb"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Bank select MSB");

        byte[] rawBytes = message.getMessage();

        int msb = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(msb);

        /*
         * Store this value (to form a combination with lsb and pc
         */
        currentMSB[message.getChannel()] = msb;

        return result;
    }

    private static boolean handleModulation(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_MODULATION);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Modulation");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handleBreath(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_BREATH);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Breath");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handlePortamentoTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_PORTAMENTO_TIME);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Portamento time");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handleDataEntryMSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_DATA_ENTRY_MSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Data entry MSB");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handleMainVolume(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_MAIN_VOLUME);

        midiEventInfo.setColor(getColor("midi", "main volume"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Main Volume");

        byte[] rawBytes = message.getMessage();

        int volume = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(volume);

        return result;
    }

    private static boolean handlePanPot(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_PANPOT);

        midiEventInfo.setColor(getColor("midi", "panpot"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Panning");

        byte[] rawBytes = message.getMessage();

        int panning = (rawBytes[2] & 0xFF) - 63;

        String display;
        if (panning == 0) {
            display = "C(0)";
        } else if (panning < 0) {
            display = "L(" + Math.abs(panning) + ")";
        } else {
            display = "R(" + panning + ")";
        }
        midiEventInfo.setComment(display);
        midiEventInfo.setValue(panning);

        return result;
    }

    private static boolean handleExpression(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_EXPRESSION);

        midiEventInfo.setColor(getColor("midi", "expression"));
        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Expression");

        byte[] rawBytes = message.getMessage();

        int expression = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(expression);

        return result;
    }

    private static boolean handleGeneralPurposeController(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_GENERAL_PURPOSE);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("GP controller");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private static boolean handleBankSelectLSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_BANK_SELECT_LSB);

        midiEventInfo.setColor(getColor("midi", "bank select lsb"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Bank Select LSB");

        byte[] rawBytes = message.getMessage();

        int lsb = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(lsb);

        /*
         * Store this value to match is with MSB and PC
         */
        currentLSB[message.getChannel()] = lsb;

        return result;
    }

    private static boolean handleDataEntryLSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleSustainDamper(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handlePortamento(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleSostenuto(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleSoftPedal(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleResonance(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleReleaseTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleAttackTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleCutoff(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleDecayTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleVibratoRate(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleVibratoDepth(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleVibratoDelay(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleArticulation1(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleArticulation2(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleArticulation3(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handlePortamentoControl(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleReverbSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleChorusSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleVariationSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleRPNIncrement(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleRPNDecrement(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleNRPNLsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleNRPNMsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleRPNLsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleRPNMsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * Mode Change Control
     */

    private static boolean handleModeChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    /**
     * Here come the Mode Change Messages
     */

    /**
     * Processing of Real-time Messages
     */
    private static boolean handlePitchBendChangeEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setEventEventKey(MIDI_PITCH_BEND);

        midiEventInfo.setColor(getColor("midi", "pitch bend"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Pitch bend");

        byte[] rawBytes = message.getMessage();
        int lsb = rawBytes[1] & 0xFF;
        int msb = rawBytes[2] & 0xFF;

        midiEventInfo.setValue((msb << 7) | lsb);

        return result;
    }

    /**
     * Real-Time Control
     */
    private static boolean processRealtimeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        byte[] rawBytes = message.getMessage();

        int command = rawBytes[0] & 0xFF;

        result = switch (command) {
            case 0xF8 -> handleMIDIClock(message, midiEventInfo);
            case 0xFA -> handleStart(message, midiEventInfo);
            case 0xFB -> handleContinue(message, midiEventInfo);
            case 0xFC -> handleStop(message, midiEventInfo);
            case 0xFE -> handleActiveSense(message, midiEventInfo);
            case 0xFF -> handleSystemReset(message, midiEventInfo);
            default -> handleUnknownRealTimeMessage(message, midiEventInfo);
        };

        return result;
    }

    /**
     * Here come the Real-Time Messages
     */
    private static boolean handleMIDIClock(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleStart(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleContinue(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleStop(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleActiveSense(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        return result;
    }

    private static boolean handleSystemReset(ShortMessage message, MidiEventInfo midiEventInfo) {
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
        midiEventInfo.setDescription("Unknown control or mode event");

        return result;
    }

    private static boolean handleUnknownRealTimeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown real-time event");

        return result;
    }

    private static boolean handleUnknownControlChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = false;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown Control Change");

        return result;
    }
}
