package util.midi.eventprocessors;

import entity.midi.MidiEventInfo;
import entity.voice.Patch;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import util.SysexToHexStringConvertor;
import util.app.AppInfo;
import util.app.colors.ColorScheme;
import util.midi.EventKeyType;
import util.midi.MidiNoteMatcher;
import util.voice.InstrumentProvider;

import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;

import static util.Util.calcNote;
import static util.app.colors.ColorScheme.getColor;
import static util.midi.EventKeyType.*;

public class MidiProcessor {
    @Getter
    private static final MidiProcessor instance = new MidiProcessor();

    private MidiProcessor() {
    }

    @Getter
    @Setter
    private int errors = 0;

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
        colors.put(new ColorScheme.ColorKey("midi", "bank select msb"), Color.DARKCYAN); // 1 of 3
        colors.put(new ColorScheme.ColorKey("midi", "bank select lsb"), Color.DARKCYAN); // 2 of 3
        colors.put(new ColorScheme.ColorKey("midi", "program change"), Color.DARKCYAN);  // 3 of 3

        colors.put(new ColorScheme.ColorKey("midi", "note"), Color.NAVAJOWHITE);

        colors.put(new ColorScheme.ColorKey("midi", "pitch bend"), Color.MEDIUMSPRINGGREEN);
        colors.put(new ColorScheme.ColorKey("midi", "main volume"), Color.LIGHTSKYBLUE);
        colors.put(new ColorScheme.ColorKey("midi", "panpot"), Color.DARKGOLDENROD);
        colors.put(new ColorScheme.ColorKey("midi", "expression"), Color.DARKORCHID);

        // Problem colors
        colors.put(new ColorScheme.ColorKey("midi", "unknown or error"), Color.RED);

        ColorScheme.addColorsToScheme(colors);

        // MSB and LSB array initialization
        java.util.Arrays.fill(currentMSB, 0);
        java.util.Arrays.fill(currentLSB, 0);
    }

    /**
     * These are used to find the matching MSB/LSB when a PC is received (all channel based)
     */
    private int getMSB(int channel) { return currentMSB[channel]; }
    private void setMSB(int channel, int value) { currentMSB[channel] = value; }
    private int getLSB(int channel) { return currentLSB[channel]; }
    private void setLSB(int channel, int value) { currentLSB[channel] = value; }

    /**
     * All these message are MIDI messages
     */
    public boolean processMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        midiEventInfo.setRootEventKey(EventKeyType.MIDI);

        midiEventInfo.setColor(getColor("midi"));
        midiEventInfo.setMessage(SysexToHexStringConvertor.convertToHexString(message.getMessage()));

        if ("91 22 76".equals(midiEventInfo.getMessage())) {
            System.out.println("BREAK");
        }

        int statusByte = message.getStatus() & 0xF0;

        return switch (statusByte) {
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
    }

    /**
     * Processing of Channel Messages
     */
    private boolean processChannelMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

        // get the raw bytes
        byte[] rawBytes = message.getMessage();

        /*
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
    private final MidiNoteMatcher midiNoteMatcher = MidiNoteMatcher.getInstance();

    private boolean handleNoteOffEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

        midiEventInfo.setEventEventKey(MIDI_NOTE_OFF);

        /*
         * We give this event a note color (this is used to close a note on,
         * so the note matching note on will be returned)
         */
        midiEventInfo.setColor(getColor("midi", "note"));
        midiEventInfo.setChannel(message.getChannel()+1);

        midiEventInfo.setDescription("Note");

        int keyNo = message.getData1(); // is the note key
        int velocity = message.getData2(); // is the velocity

        /*
         * The note ('C#', ...) is displayed in the comment
         */
        midiEventInfo.setComment(calcNote(keyNo).getName());

        /*
         * The velocity comes in the value
         */
        midiEventInfo.setValue(velocity);

        /*
         * look for matching note on
         */
        result = midiNoteMatcher.processEvent(message, midiEventInfo);

        return result;
    }

    private boolean handleNoteOnEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

        midiEventInfo.setEventEventKey(MIDI_NOTE_ON);

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

        int keyNo = message.getData1(); // is the note key
        int velocity = message.getData2(); // is the velocity

        /*
         * The note ('C#', ...) is displayed in the comment
         */
        midiEventInfo.setComment(calcNote(keyNo).getName());
        /*
         * The velocity comes in the value
         */
        midiEventInfo.setValue(velocity);

        /*
         * If it is a note On with velocity > 0 -> Store it
         * If it is a note On with velocaity = 0 -> behaves like a note off
         */
        result = midiNoteMatcher.processEvent(message, midiEventInfo);

        return result;
    }

    private boolean handleControlChangeOrModeMessageEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

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
            case 0x56 -> false;
            default -> handleUnknownControlOrModeMessage(message, midiEventInfo);

        };

        return result;
    }

    private boolean handleProgramChangeEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_PROGRAM_CHANGE);

        /*
         * Program change get a color (because it is important)
         */
        midiEventInfo.setColor(getColor("midi", "program change"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Program change");

        byte[] rawBytes = message.getMessage();
        int pc = rawBytes[1] & 0xFF;

        // voice number
        midiEventInfo.setValue(pc);

        /*
         * Lookup the matching MSB/LSB value which together with PC defines voice
         */
        int msb = getMSB(message.getChannel());
        int lsb = getLSB(message.getChannel());

        String voice = searchVoice(msb, lsb, pc);
        midiEventInfo.setComment(voice);

        return result;
    }

    private boolean handleChannelAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_CHANNEL_AFTERTOUCH);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Channel aftertouch");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[1] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handlePolyphonicAftertouchEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

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
    private boolean handleControlChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

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
    private boolean handleBankSelectMSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

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
        setMSB(message.getChannel(), msb);

        return result;
    }

    private boolean handleModulation(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_MODULATION);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Modulation");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleBreath(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_BREATH);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Breath");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handlePortamentoTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_PORTAMENTO_TIME);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Portamento time");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleDataEntryMSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_DATA_ENTRY_MSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Data entry MSB");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleMainVolume(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_MAIN_VOLUME);

        midiEventInfo.setColor(getColor("midi", "main volume"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Main Volume");

        byte[] rawBytes = message.getMessage();

        int volume = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(volume);

        return result;
    }

    private boolean handlePanPot(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_PANPOT);

        midiEventInfo.setColor(getColor("midi", "panpot"));

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Panning");

        byte[] rawBytes = message.getMessage();

        int panning = (rawBytes[2] & 0xFF) - 64;

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

    private boolean handleExpression(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_EXPRESSION);

        midiEventInfo.setColor(getColor("midi", "expression"));
        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Expression");

        byte[] rawBytes = message.getMessage();

        int expression = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(expression);

        return result;
    }

    private boolean handleGeneralPurposeController(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_GENERAL_PURPOSE);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("GP controller");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleBankSelectLSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

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
        setLSB(message.getChannel(), lsb);

        return result;
    }

    private boolean handleDataEntryLSB(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_DATA_ENTRY_LSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Data entry LSB");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleSustainDamper(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_SUSTAIN_DAMPER);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Sustain damper");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handlePortamento(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_PORTAMENTO);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Portamento");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setComment(data < 64 ? "OFF" : "ON");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleSostenuto(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_SOSTENUTO);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Sostenuto");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[2];

        midiEventInfo.setComment(data < 64 ? "OFF" : "ON");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleSoftPedal(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_SOFT_PEDAL);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Soft pedal");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[2];

        midiEventInfo.setComment(data < 64 ? "OFF" : "ON");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleResonance(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RESONANCE);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Resonance");

        byte[] rawBytes = message.getMessage();

        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleReleaseTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RELEASE_TIME);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Release time");

        byte[] rawBytes = message.getMessage();

        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleAttackTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ATTACK_TIME);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Attack time");

        byte[] rawBytes = message.getMessage();

        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleCutoff(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_CUTOFF);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Cutoff");

        byte[] rawBytes = message.getMessage();

        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleDecayTime(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_DECAY_TIME);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Decay time");

        byte[] rawBytes = message.getMessage();
        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleVibratoRate(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_VIBRATO_RATE);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Vibrato rate");

        byte[] rawBytes = message.getMessage();

        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleVibratoDepth(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_VIBRATO_DEPTH);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("vibrato depth");

        byte[] rawBytes = message.getMessage();
        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleVibratoDelay(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_VIBRATO_DELAY);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Vibrato delay");

        byte[] rawBytes = message.getMessage();
        int data = (rawBytes[2] & 0xFF) - 64;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleArticulation1(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ARTICULATION_1);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Articulation 1");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[2];

        midiEventInfo.setComment(data == 0 ? "OFF" : data == 127 ? "ON" : "Limit ERROR");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleArticulation2(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ARTICULATION_2);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Articulation 2");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[2];

        midiEventInfo.setComment(data == 0 ? "OFF" : data == 127 ? "ON" : "Limit ERROR");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleArticulation3(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ARTICULATION_3);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Articulation 3");

        byte[] rawBytes = message.getMessage();
        int data = rawBytes[2];
        midiEventInfo.setComment(data == 0 ? "OFF" : data == 127 ? "ON" : "Limit ERROR");
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handlePortamentoControl(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_PORTAMENTO_CONTROL);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Portamento control");

        byte[] rawBytes = message.getMessage();

        int keyNo = rawBytes[2] & 0xFF;

        midiEventInfo.setComment(calcNote(keyNo).getName());
        midiEventInfo.setValue(keyNo);

        return result;
    }

    private boolean handleReverbSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_REVERB_SEND_LEVEL);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Reverb send level");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleChorusSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_CHORUS_SEND_LEVEL);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Chorus send level");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleVariationSendLevel(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_VARIATION_SEND_LEVEL);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("Variation send level");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleRPNIncrement(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RPN_INCREMENT);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("RPN increment");

        return result;
    }

    private boolean handleRPNDecrement(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RPN_DECREMENT);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("RPN decrement");

        return result;
    }

    private boolean handleNRPNLsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_NRPN_LSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("NRPN LSB");

        byte[] rawBytes = message.getMessage();

        int lsb = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(lsb);

        return result;
    }

    private boolean handleNRPNMsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_NRPN_MSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("NRPN MSB");

        byte[] rawBytes = message.getMessage();

        int data = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(data);

        return result;
    }

    private boolean handleRPNLsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RPN_LSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("RPN LSB");

        byte[] rawBytes = message.getMessage();

        int lsb = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(lsb);

        return result;
    }

    private boolean handleRPNMsb(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RPN_MSB);

        midiEventInfo.setChannel(message.getChannel()+1);
        midiEventInfo.setDescription("RPN MSB");

        byte[] rawBytes = message.getMessage();

        int msb = rawBytes[2] & 0xFF;
        midiEventInfo.setValue(msb);

        return result;
    }

    /**
     * Mode Change Control
     */

    private boolean handleModeChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

        byte[] rawBytes = message.getMessage();

        int byte1 = rawBytes[1] & 0xFF;

        result = switch (byte1) {
            case 0x78 -> handleAllSoundOff(message, midiEventInfo);
            case 0x79 -> handleResetAllControllers(message, midiEventInfo);
            case 0x7A -> handleLocalControl(message, midiEventInfo);
            case 0x7B -> handleAllNoteOff(message, midiEventInfo);
            case 0x7C -> handleOmniOff(message, midiEventInfo);
            case 0x7D -> handleOmniOn(message, midiEventInfo);
            case 0x7E -> handleMono(message, midiEventInfo);
            case 0x7F -> handlePoly(message, midiEventInfo);
            default -> handleUnknownModeChangeMessage(message, midiEventInfo);
        };

        return result;
    }

    /**
     * Here come the Mode Change Messages
     */
    private boolean handleAllSoundOff(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ALL_SOUND_OFF);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("All sound off");

        return result;
    }

    private boolean handleResetAllControllers(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_RESET_ALL_CONTROLLERS);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Reset all controllers");

        return result;
    }

    private boolean handleLocalControl(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_LOCAL_CONTROL);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Local control");

        return result;
    }

    private boolean handleAllNoteOff(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ALL_NOTE_OFF);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("All note off");

        return result;
    }

    private boolean handleOmniOff(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_OMNI_OFF);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Omni off");

        return result;
    }

    private boolean handleOmniOn(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_OMNI_ON);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Omni on");

        return result;
    }

    private boolean handleMono(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_MONO);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Mono");

        return result;
    }

    private boolean handlePoly(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_POLY);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Poly");

        return result;
    }

    /**
     * Processing of Real-time Messages
     */
    private boolean handlePitchBendChangeEvent(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

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
    private boolean processRealtimeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result;

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
    private boolean handleMIDIClock(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_CLOCK);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("clock");

        return result;
    }

    private boolean handleStart(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_START);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Start");

        return result;
    }

    private boolean handleContinue(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_CONTINUE);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Continue");

        return result;
    }

    private boolean handleStop(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_STOP);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Stop");

        return result;
    }

    private boolean handleActiveSense(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_ACTIVE_SENSE);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Active sense");

        return result;
    }

    private boolean handleSystemReset(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(MIDI_SYSTEM_RESET);

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("System reset");

        return result;
    }

    /**
         * From here the problem cases
         */
    private boolean processUnknowMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown midi event");

        return result;
    }

    private boolean handleInvalidChannelMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown midi event");

        return result;
    }

    private boolean handleUnknownControlOrModeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown control or mode event");

        return result;
    }

    private boolean handleUnknownRealTimeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown real-time event");

        return result;
    }

    private boolean handleUnknownControlChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown Control Change");

        return result;
    }

    private boolean handleUnknownModeChangeMessage(ShortMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        errors++;

        midiEventInfo.setColor(getColor("midi", "unknown or error"));

        midiEventInfo.setChannel(message.getChannel() + 1);
        midiEventInfo.setDescription("Unknown Mode Change");

        return result;
    }

    private static String searchVoice(int msb, int lsb, int pc) {
        String voice = "Unknown voice";

        InstrumentProvider instrumentProvider = AppInfo.INSTANCE.getInstrumentProvider();

        if (instrumentProvider != null) {
            Patch patch = instrumentProvider.findPatch(msb, lsb, pc);
            if (patch != null) {
                voice = patch.getName();
            }
        }

        return voice;
    }
}
