package util.midi.eventprocessors;

import entity.midi.MidiEventInfo;
import javafx.scene.paint.Color;
import lombok.Getter;
import util.ByteHelper;
import util.SysexToHexStringConvertor;
import util.app.colors.ColorScheme;
import util.midi.EventKeyType;
import util.midi.MidiNoteMatcher;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.util.HashMap;
import java.util.Map;

import static util.Util.calcNote;
import static util.app.colors.ColorScheme.getColor;
import static util.midi.EventKeyType.*;

public class SysexProcessor {
    @Getter
    private static final SysexProcessor instance = new SysexProcessor();

    private SysexProcessor() {
    }

    /**
     * These are colors specific to midi events (they are added to the colorScheme)
     * - midi events are normally BLUE except the ones that are unknown/invalid which are RED
     *
     */
    private static final Map<ColorScheme.ColorKey, Color> colors = new HashMap<>();

    static {
        // Global Color
        colors.put(new ColorScheme.ColorKey("sysex"), Color.IVORY);

        // Event Colors

        // Problem colors
        colors.put(new ColorScheme.ColorKey("sysex", "unknown or error"), Color.RED);

        ColorScheme.addColorsToScheme(colors);
    }

    /**
     * All these message are SYSEX messages
     */
    public boolean processMessage(SysexMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setRootEventKey(SYSEX);

        StringBuilder sysex = SysexToHexStringConvertor.convertToHexStringbuilder(message.getMessage());

        midiEventInfo.setColor(getColor("sysex"));
        midiEventInfo.setDescription("Sysex");

        midiEventInfo.setMessage(sysex.toString().stripTrailing());

        String selector = ByteHelper.getAndStrip(sysex, 1);

        result = switch (selector) {
            case "F0" -> handleSysexMessage(sysex, midiEventInfo);
            default -> handleUnknownSysexMessage(selector, midiEventInfo);
        };

        return true;
    }

    /*
     * FO ...
     */
    private static boolean handleSysexMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result;

        String selector = ByteHelper.getAndStrip(sysex, 1);

        result = switch(selector) {
            case "7F" -> handleUniversalRealTimeMessage(sysex, midiEventInfo);
            case "7E" -> handleUniversalNonRealTimeMessage(sysex, midiEventInfo);
            case
                    "41",
                    "43" -> handleManufacturerMessage(selector, sysex, midiEventInfo);
            default -> handleUnknownSysexMessage("F0 " + selector, midiEventInfo);
        };

        return result;
    }

    /*
     * FO 7F...
     */
    private static boolean handleUniversalRealTimeMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = false;

        // skip the XN bytes
        ByteHelper.getAndStrip(sysex, 1);

        String selector = ByteHelper.getAndStrip(sysex, 2);

        result = switch (selector) {
            case "04 01" -> handleMasterVolume(sysex, midiEventInfo);
            case "04 03" -> handleMasterFineTuning(sysex, midiEventInfo);
            case "04 04" -> handleMasterCoarseTuning(sysex, midiEventInfo);
            case "04 05" -> handleReverbChorusParameter(sysex, midiEventInfo);
            case "09 01" -> handleChannelPressure(sysex, midiEventInfo);
            case "09 03" -> handleControlChange(sysex, midiEventInfo);
            case "0A 01" -> handleKeybasedInstrumentControl(sysex, midiEventInfo);
            default -> handleUnknowUniversalRealTimeMessage(selector, midiEventInfo);
        };

        return result;
    }

    /**
     * F0 7F XN 04 01...
     */
    private static boolean handleMasterVolume(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_MASTER_VOLUME);
        midiEventInfo.setDescription("Master volume");

        int lsb = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);
        int msb = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);

        int value = (msb << 7) | lsb;
        midiEventInfo.setValue(value);

        return result;
    }

    /**
     * F0 7F XN 04 03...
     */
    private static boolean handleMasterFineTuning(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_MASTER_FINE_TUNING);
        midiEventInfo.setDescription("Master fine tuning");

        int lsb = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);
        int msb = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);

        int value = (msb << 7) | lsb;
        midiEventInfo.setValue(value);

        return result;
    }

    /**
     * F0 7F XN 04 04...
     */
    private static boolean handleMasterCoarseTuning(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_MASTER_COARSE_TUNING);
        midiEventInfo.setDescription("Master coarse tuning");

        ByteHelper.getAndStrip(sysex,1);

        int lsb = 0;
        int msb = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);

        int value = (msb << 7) | lsb;
        midiEventInfo.setValue(value);

        return result;
    }

    /**
     * F0 7F XN 04 05...
     */
    private static boolean handleReverbChorusParameter(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        // Strip the 01 01 01 01
        ByteHelper.getAndStrip(sysex, 4);

        String selector = ByteHelper.getAndStrip(sysex, 1);
        result = switch (selector) {
            case "01" -> handleReverbParameter(sysex, midiEventInfo);
            case "02" -> handleChorusParameter(sysex, midiEventInfo);
            default -> handleUnknownEffectParameter(selector, midiEventInfo);
        };

        return result;
    }

    /**
     * F0 7F XN 04 05 01 01 01 01 01 ...
     */
    private static boolean handleReverbParameter(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_REVERB_PARAMETER);
        midiEventInfo.setDescription("Reverb Parameter");

        return result;
    }

    /**
     * F0 7F XN 04 05 01 01 01 01 02 ...
     */
    private static boolean handleChorusParameter(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_CHORUS_PARAMETER);
        midiEventInfo.setDescription("Chorus Parameter");

        return result;
    }

    /**
     * F0 7F XN 09 01...
     */
    private static boolean handleChannelPressure(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_CHANNEL_PRESSURE);
        midiEventInfo.setDescription("Channel Pressure (Aftertouch)");

        int midiChannel = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16) & 0x0F;
        midiEventInfo.setChannel(midiChannel+1);

        return result;
    }

    /**
     * F0 7F XN 09 03...
     */
    private static boolean handleControlChange(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_CONTROLLER);
        midiEventInfo.setDescription("Controller (Control Change)");

        int midiChannel = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16) & 0x0F;
        midiEventInfo.setChannel(midiChannel+1);

        return result;
    }

    /**
     * F0 7F XN 0A 01...
     */
    private static boolean handleKeybasedInstrumentControl(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_KEYBASED_INSTRUMENT_CONTROL);
        midiEventInfo.setDescription("Keybased Instrument Control");

        int midiChannel = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16) & 0x0F;
        midiEventInfo.setChannel(midiChannel+1);

        int keyNo = Integer.parseInt(ByteHelper.getAndStrip(sysex,1), 16);
        midiEventInfo.setComment(calcNote(keyNo).getName());

        return result;
    }

    /*
     * FO 7E...
     */
    private static boolean handleUniversalNonRealTimeMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = false;

        // skip the XN bytes
        ByteHelper.getAndStrip(sysex, 1);

        String selector = ByteHelper.getAndStrip(sysex, 2);

        result = switch (selector) {
            case "09 01" -> handleGM1SystemOn(sysex, midiEventInfo);
            case "09 03" -> handleGM2SystemOn(sysex, midiEventInfo);
            case "09 02" -> handleGeneralMidiSystemOff(sysex, midiEventInfo);
            case "08 08" -> handleScaleOctaveTuning(sysex, midiEventInfo);
            default -> handleUnknowUniversalNonRealTimeMessage(selector, midiEventInfo);
        };

        return result;
    }

    /**
     * F0 7E XN 09 01...
     */
    private static boolean handleGM1SystemOn(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_GM1_SYSTEM_ON);
        midiEventInfo.setDescription("GM1 System On");

        return result;
    }

    /**
     * F0 7E XN 09 03...
     */
    private static boolean handleGM2SystemOn(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_GM2_SYSTEM_ON);
        midiEventInfo.setDescription("GM1 System On");

        return result;
    }

    /**
     * F0 7E XN 09 02...
     */
    private static boolean handleGeneralMidiSystemOff(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_GM_SYSTEM_OFF);
        midiEventInfo.setDescription("GM System Off");

        return result;
    }

    /**
     * F0 7E XN 08 08...
     */
    private static boolean handleScaleOctaveTuning(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_SCALE_OCTAVE_TUNING);
        midiEventInfo.setDescription("Scale/Octave Tuning");

        return result;
    }

    /*
     * FO 43(or 41)...
     */
    private static boolean handleManufacturerMessage(String selector, StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = false;

        result = switch (selector) {
            case "43" -> handleYamahaMessage(sysex, midiEventInfo);
            case "42" -> handleKorgMessage(sysex, midiEventInfo);
            case "41" -> handleRolandMessage(sysex, midiEventInfo);
            default -> handleUnknowManufacturerMessage(selector, midiEventInfo);
        };

        return result;
    }

    /*
     * FO 41...
     */
    private static boolean handleRolandMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setDescription("Roland not supported");

        return  result;
    }

    /*
     * FO 42...
     */
    private static boolean handleKorgMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setDescription("Korg not supported");

        return  result;
    }

    /*
     * FO 43...
     */
    private static boolean handleYamahaMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        
        return  result;
    }

    /*
     * From here the problem cases
     */
    private static boolean handleUnknownSysexMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));
        midiEventInfo.setDescription("Unknown sysex");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknowUniversalRealTimeMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));
        midiEventInfo.setDescription("Unknown URT message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownEffectParameter(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));
        midiEventInfo.setDescription("Unknown Parameter message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknowUniversalNonRealTimeMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));
        midiEventInfo.setDescription("Unknown Non-URT message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknowManufacturerMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));
        midiEventInfo.setDescription("Unknown Manufacturer message");
        midiEventInfo.setComment(selector);

        return result;
    }
}
