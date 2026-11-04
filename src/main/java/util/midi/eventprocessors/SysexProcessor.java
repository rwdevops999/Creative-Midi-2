package util.midi.eventprocessors;

import entity.midi.MidiEventInfo;
import javafx.scene.paint.Color;
import lombok.Getter;
import util.ByteHelper;
import util.SysexToHexStringConvertor;
import util.app.colors.ColorScheme;

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
            case "41",
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

        midiEventInfo.setDescription("Roland Message");
        midiEventInfo.setComment("Not Supported");

        return  result;
    }

    /*
     * FO 42...
     */
    private static boolean handleKorgMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setDescription("Korg Message");
        midiEventInfo.setComment("Not Supported");

        return  result;
    }

    /*
     * FO 43...
     */
    private static boolean handleYamahaMessage(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        String selector = ByteHelper.getAndStrip(sysex, 2);

        result = switch (selector) {
            case "7E" -> handleYamahaControls(sysex, midiEventInfo);
            case "73" -> handleYamahaSystemMessages(sysex, midiEventInfo);
            case "10",
                 "11",
                 "12",
                 "13",
                 "14",
                 "15",
                 "16",
                 "17",
                 "18",
                 "19",
                 "1A",
                 "1B",
                 "1C",
                 "1D",
                 "1E",
                 "1F" -> handleYamahaXGParameterChange(sysex, midiEventInfo);

            default -> handleUnknownYamahaMessage(selector, midiEventInfo);
        };

        return  result;
    }

    /*
     * FO 43 7E...
     */
    private static boolean handleYamahaControls(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        String selector = ByteHelper.getAndStrip(sysex, 2);
        result = switch (selector) {
            case "00" -> handleSectionControl(sysex, midiEventInfo);
            case "01" -> handleTempoControl(sysex, midiEventInfo);
            case "02" -> handleChordControlType1(sysex, midiEventInfo);
            case "03" -> handleChordControlType2(sysex, midiEventInfo);
            default -> handleUnknownYamahaControl(selector, midiEventInfo);
        };

        return result;
    }

    /*
     * FO 43 7E 00...
     */
    private static boolean handleSectionControl(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_SECTION_CONTROL);
        midiEventInfo.setDescription("Section Control");

        String switchNo = ByteHelper.getAndStrip(sysex, 1);
        String action = ByteHelper.getAndStrip(sysex, 1);

        String switchName = switch (switchNo) {
            case "00" -> "Intro 1";
            case "01" -> "Intro 2";
            case "02" -> "Intro 3";
            case "03" -> "Intro 4";
            case "08" -> "Main A";
            case "09" -> "Main B";
            case "0A" -> "Main C";
            case "0B" -> "Main D";
            case "10" -> "Fill In AA";
            case "11" -> "Fill In BB";
            case "12" -> "Fill In CC";
            case "13" -> "Fill In DD";
            case "18" -> "Break Fill";
            case "20" -> "Ending 1";
            case "21" -> "Ending 2";
            case "22" -> "Ending 3";
            case "23" -> "Ending 4";
            default -> "Unknown";
        };

        String actionName = switch(action) {
            case "00" -> "OFF";
            case "7F" -> "ON";
            default -> "--";
        };

        midiEventInfo.setComment(switchName + " = " + actionName);

        return result;
    }

    /*
     * FO 43 7E 01...
     */
    private static boolean handleTempoControl(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_TEMPO_CONTROL);
        midiEventInfo.setDescription("Tempo Control");

        int t4 = Integer.parseInt(ByteHelper.getAndStrip(sysex, 1));
        int t3 = Integer.parseInt(ByteHelper.getAndStrip(sysex, 1));
        int t2 = Integer.parseInt(ByteHelper.getAndStrip(sysex, 1));
        int t1 = Integer.parseInt(ByteHelper.getAndStrip(sysex, 1));

        long microSecondsPerQuarterNote = ((long) (t4 & 0x7F) << 21) |
                ((long) (t3 & 0x7F) << 14) |
                ((long) (t2 & 0x7F) << 7)  |
                ((long) (t1 & 0x7F));


        if (microSecondsPerQuarterNote == 0) {
            midiEventInfo.setValue(0);
        } else {
            midiEventInfo.setValue((int)Math.round(60000000.0 / microSecondsPerQuarterNote));
        }

        return result;
    }

    /*
     * FO 43 7E 02...
     */
    private static boolean handleChordControlType1(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_CHORD_CONTROL_1);
        midiEventInfo.setDescription("Chord Control (Type 1)");

        return result;
    }

    /*
     * FO 43 7E 03...
     */
    private static boolean handleChordControlType2(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_CHORD_CONTROL_2);
        midiEventInfo.setDescription("Chord Control (Type 2)");

        return result;
    }
    /*
     * FO 43 73...
     */
    private static boolean handleYamahaSystemMessages(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        /*
         * Skip the 01 byte
         */
        ByteHelper.getAndStrip(sysex, 1);

        String selector = ByteHelper.getAndStrip(sysex, 1);

        result = switch (selector) {
            case "52" -> handleScaleMessages(sysex, midiEventInfo);
            case "02" -> handleInternalClock(sysex, midiEventInfo);
            case "03" -> handleExternalClock(sysex, midiEventInfo);
            case "51" -> handleStyleMessages(sysex, midiEventInfo);
            case "1F" -> handleGuideMessages(sysex, midiEventInfo);
            case "50" -> handleScoreMessages(sysex, midiEventInfo);
            default -> handleUnknownYamahaSystemMessage(selector, midiEventInfo);
        };

        return result;
    }

    /*
     * FO 43 73 01 52...
     */
    private static boolean handleScaleMessages(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        /*
         * Skip the 2F 02 bytes
         */
        ByteHelper.getAndStrip(sysex, 2);
        String selector = ByteHelper.getAndStrip(sysex, 1);

        result = switch (selector) {
            case
                "00",
                "01" -> handleScaleTuningMessages(selector, sysex, midiEventInfo);
            default -> handleUnknownScaleMessages(selector, midiEventInfo);
        };

        return result;
    }

    /*
     * FO 43 73 01 73 2F 02 (00/01)...
     */
    private static boolean handleScaleTuningMessages(String type, StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        String selector = ByteHelper.getAndStrip(sysex, 1);

        result = switch (selector) {
            case "40" -> handleScaleTuning(type, sysex, midiEventInfo);
            case "4D" -> handleScaleTuningBaseNote(type, sysex, midiEventInfo);
            case "4F" -> {
                midiEventInfo.setEventEventKey(SYSEX_TUNING_PART);
                midiEventInfo.setDescription("Scale Tuning Part");
                midiEventInfo.setComment("LEFT");
                yield true;
            }
            case "50" -> {
                midiEventInfo.setEventEventKey(SYSEX_TUNING_PART);
                midiEventInfo.setDescription("Scale Tuning Part");
                midiEventInfo.setComment("STYLE");
                yield true;
            }
            case "51" -> {
                midiEventInfo.setEventEventKey(SYSEX_TUNING_PART);
                midiEventInfo.setDescription("Scale Tuning Part");
                midiEventInfo.setComment("MULTI_PAD");
                yield true;
            }
            case "52" -> {
                midiEventInfo.setEventEventKey(SYSEX_TUNING_PART);
                midiEventInfo.setDescription("Scale Tuning Part");
                midiEventInfo.setComment("RIGHT1");
                yield true;
            }
            case "53" -> {
                midiEventInfo.setEventEventKey(SYSEX_TUNING_PART);
                midiEventInfo.setDescription("Scale Tuning Part");
                midiEventInfo.setComment("RIGHT2");
                yield true;
            }
            default -> handleUnknownScaleTuningMessage(selector, midiEventInfo);

        };
        return result;
    }

    /*
     * FO 43 73 01 73 2F 02 (00/01) 40...
     */
    private static boolean handleScaleTuning(String type, StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_SCALE_TUNING);
        midiEventInfo.setDescription("Scale Tuning");
        midiEventInfo.setComment(type);

        return result;
    }

    /*
     * FO 43 73 01 73 2F 02 (00/01) 4D...
     */
    private static boolean handleScaleTuningBaseNote(String type, StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_SCALE_TUNING_BASE_NOTE);
        midiEventInfo.setDescription("Scale Tuning Base Note");

        int keyNo = Integer.parseInt(ByteHelper.getAndStrip(sysex, 1));
        String mode = ByteHelper.getAndStrip(sysex, 1);

        String modeName = switch (mode) {
            case "00" -> "Base Note Only";
            case "7F" -> "Base Note and Tune setting";
            default -> "Unknown";
        };

        midiEventInfo.setComment(calcNote(keyNo).getName() + " = " + modeName);
        midiEventInfo.setComment(type);

        return result;
    }

    /*
     * FO 43 73 01 02...
     */
    private static boolean handleInternalClock(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_INTERNAL_CLOCK);
        midiEventInfo.setDescription("Internal Clock");

        return result;
    }

    /*
     * FO 43 73 01 03...
     */
    private static boolean handleExternalClock(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_EXTERNAL_CLOCK);
        midiEventInfo.setDescription("External Clock");

        return result;
    }

    /*
     * FO 43 73 01 51...
     */
    private static boolean handleStyleMessages(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        // F0 43 73 01 51 00 00 02 05 ... Display Control
        // F0 43 73 01 51 00 00 03 10 ... Style Split Point
        // F0 43 73 01 51 05 00 04 00 ... Style No

        return result;
    }

    /*
     * FO 43 73 01 1F...
     */
    private static boolean handleGuideMessages(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setEventEventKey(SYSEX_GUIDE);
        midiEventInfo.setDescription("Guide");

        String part = ByteHelper.getAndStrip(sysex, 1);
        String mode = ByteHelper.getAndStrip(sysex, 1);

        String partName = switch (part) {
            case "00" -> "R+L";
            case "01" -> "L";
            case "02" -> "R";
            case "03" -> "";
            default -> "-";
        };

        String modeName = switch (mode) {
            case "00" -> "OFF";
            case "01" -> "Follow Lights";
            case "02" -> "Any Key";
            case "03" -> "Karao Key";
            case "04" -> "Vocal Cue Time";
            case "06" -> "Your Tempo";
            default -> "-";
        };

        midiEventInfo.setComment(partName + ":" + modeName);

        return result;
    }

    /*
     * FO 43 73 01 50...
     */
    private static boolean handleScoreMessages(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        String selector = ByteHelper.getAndStrip(sysex, 3);
        result = switch (selector) {
            case "19 00 00" -> {
                midiEventInfo.setEventEventKey(SYSEX_SONG_CONTROL);
                midiEventInfo.setDescription("Audio Song Control");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                String modeName = switch (mode) {
                    case "00" -> "Start";
                    case "01" -> "Stop";
                    case "02" -> "Pause";
                    default -> "-";
                };

                yield true;
            }
            case "12 00 00" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Left Indication");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 01" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Right Indication");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 02" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Lyrics Indication");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 03" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Chord Indication");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 04" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Note Name Indication");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 05" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Size");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("Small");
                } else if ("02".equals(mode)) {
                    midiEventInfo.setComment("Large");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 06" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Left Ch");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                String modeName = switch (mode) {
                    case "00" -> "Channel 1";
                    case "01" -> "Channel 2";
                    case "02" -> "Channel 3";
                    case "03" -> "Channel 4";
                    case "04" -> "Channel 5";
                    case "05" -> "Channel 6";
                    case "06" -> "Channel 7";
                    case "07" -> "Channel 8";
                    case "08" -> "Channel 9";
                    case "09" -> "Channel 10";
                    case "0A" -> "Channel 11";
                    case "0B" -> "Channel 12";
                    case "0C" -> "Channel 13";
                    case "0D" -> "Channel 14";
                    case "0E" -> "Channel 15";
                    case "0F" -> "Channel 16";
                    case "7E" -> "OFF";
                    case "7F" -> "AUTO";
                    default -> "---";
                };

                midiEventInfo.setComment(modeName);

                yield true;
            }
            case "12 00 07" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Right Ch");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                String modeName = switch (mode) {
                    case "00" -> "Channel 1";
                    case "01" -> "Channel 2";
                    case "02" -> "Channel 3";
                    case "03" -> "Channel 4";
                    case "04" -> "Channel 5";
                    case "05" -> "Channel 6";
                    case "06" -> "Channel 7";
                    case "07" -> "Channel 8";
                    case "08" -> "Channel 9";
                    case "09" -> "Channel 10";
                    case "0A" -> "Channel 11";
                    case "0B" -> "Channel 12";
                    case "0C" -> "Channel 13";
                    case "0D" -> "Channel 14";
                    case "0E" -> "Channel 15";
                    case "0F" -> "Channel 16";
                    case "7E" -> "OFF";
                    case "7F" -> "AUTO";
                    default -> "---";
                };

                midiEventInfo.setComment(modeName);

                yield true;
            }
            case "12 00 08" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Quantize Triplet");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            case "12 00 09" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Quantize");

                String quantize = ByteHelper.getAndStrip(sysex, 1);
                String quantizeName = switch (quantize) {
                    case "00" -> "Quarter";
                    case "01" -> "Eighth";
                    case "02" -> "Sixteenth";
                    case "03" -> "Thirty-Second";
                    default -> "---";
                };
                midiEventInfo.setComment(quantizeName);

                yield true;
            }
            case "12 00 0A" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Note Name");

                String format = ByteHelper.getAndStrip(sysex, 1);
                String formatName = switch (format) {
                    case "00" -> "A, B, C";
                    case "01" -> "Fixed Do";
                    case "02" -> "Movable Do";
                    default -> "---";
                };
                midiEventInfo.setComment(formatName);

                yield true;
            }
            case "12 00 0B" -> {
                midiEventInfo.setEventEventKey(SYSEX_SCORE_CONTROL);
                midiEventInfo.setDescription("Color Note");

                String mode = ByteHelper.getAndStrip(sysex, 1);
                if ("00".equals(mode)) {
                    midiEventInfo.setComment("OFF");
                } else if ("7F".equals(mode)) {
                    midiEventInfo.setComment("ON");
                } else {
                    midiEventInfo.setComment("--");
                }

                yield true;
            }
            default -> handleUnknownScoreMessage(selector, midiEventInfo);
        };
        // FO 43 73 01 50 19 00 00     Audio Song Control
        // FO 43 73 01 50 12 00 (00-0B)

        return result;
    }

    /*
     * FO 43 1n...
     */
    private static boolean handleYamahaXGParameterChange(StringBuilder sysex, MidiEventInfo midiEventInfo) {
        boolean result = true;

        // Skip byte 4C
        ByteHelper.getAndStrip(sysex, 1);

        midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);

        String address = ByteHelper.getAndStrip(sysex, 2);

        result = switch (address) {
            case "00 00" -> {
                midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);
                midiEventInfo.setDescription("XG System");
                yield true;
            }
            case "01 00" -> {
                midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);
                midiEventInfo.setDescription("System Info");
                yield true;
            }
            case "02 01" -> {
                midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);
                midiEventInfo.setDescription("Effect1");

                String kind = ByteHelper.getAndStrip(sysex, 1);
                String kindName = switch (kind) {
                    case "00" -> "Reverb";
                    case "20" -> "Chorus";
                    case "40" -> "Variation";
                    default -> "Parameter";
                };
                midiEventInfo.setComment(kindName);

                yield true;
            }
            case "02 40" -> {
                midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);
                midiEventInfo.setDescription("Multi EQ");

                yield true;
            }
            default -> {
                midiEventInfo.setEventEventKey(SYSEX_XG_PARAMETER_CHANGE);
                midiEventInfo.setDescription("Unknown");
                midiEventInfo.setComment(address);

                yield false;
            }
        };

        return result;
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

    private static boolean handleUnknownYamahaMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Yamaha message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownYamahaControl(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Yamaha Control");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownYamahaSystemMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Yamaha System Message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownScaleMessages(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Scale Message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownScaleTuningMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Scale Tuning Message");
        midiEventInfo.setComment(selector);

        return result;
    }

    private static boolean handleUnknownScoreMessage(String selector, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setColor(getColor("sysex", "unknown or error"));

        midiEventInfo.setDescription("Unknown Score Message");
        midiEventInfo.setComment(selector);

        return result;
    }
}
