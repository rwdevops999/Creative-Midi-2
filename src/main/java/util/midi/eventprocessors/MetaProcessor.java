package util.midi.eventprocessors;

import entity.midi.MidiEventInfo;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import util.SysexToHexStringConvertor;
import util.app.colors.ColorScheme;
import util.midi.EventKeyType;

import javax.sound.midi.MetaMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static util.app.colors.ColorScheme.getColor;
import static util.midi.EventKeyType.*;

public class MetaProcessor {
    @Getter
    private static final MetaProcessor instance = new MetaProcessor();

    @Getter
    @Setter
    private int errors = 0;

    private MetaProcessor() {
    }

    /**
     * These are colors specific to midi events (they are added to the colorScheme)
     * - midi events are normally BLUE except the ones that are unknown/invalid which are RED
     *
     */
    private static final Map<ColorScheme.ColorKey, Color> colors = new HashMap<>();
    private static final Map<String, String> MANUFACTUROR_REGISTRY = new HashMap<>();

    static {
        // Global Color
        colors.put(new ColorScheme.ColorKey("meta"), Color.INDIGO);

        // Event Colors
        colors.put(new ColorScheme.ColorKey("meta", "tempo"), Color.SEAGREEN);
        colors.put(new ColorScheme.ColorKey("meta", "time signature"), Color.SEAGREEN);
        colors.put(new ColorScheme.ColorKey("meta", "key signature"), Color.SEAGREEN);
        colors.put(new ColorScheme.ColorKey("meta", "marker"), Color.DARKTURQUOISE.darker());

        // Problem colors
        colors.put(new ColorScheme.ColorKey("meta", "unknown or error"), Color.RED);

        ColorScheme.addColorsToScheme(colors);

        // --- 1-Byte Standard IDs ---
        MANUFACTUROR_REGISTRY.put("0x01", "Sequential Circuits");
        MANUFACTUROR_REGISTRY.put("0x04", "Moog Music");
        MANUFACTUROR_REGISTRY.put("0x06", "Lexicon");
        MANUFACTUROR_REGISTRY.put("0x41", "Roland Corporation");
        MANUFACTUROR_REGISTRY.put("0x42", "Korg Inc.");
        MANUFACTUROR_REGISTRY.put("0x43", "Yamaha Corporation");
        MANUFACTUROR_REGISTRY.put("0x47", "Akai Professional");

        // --- 3-Byte Extended IDs (0x00XXXX) ---
        MANUFACTUROR_REGISTRY.put("0x00000E", "Apple Inc.");
        MANUFACTUROR_REGISTRY.put("0x000041", "Cakewalk / Twelve Tone Systems");
        MANUFACTUROR_REGISTRY.put("0x00010B", "Propellerhead Software");
        MANUFACTUROR_REGISTRY.put("0x00015B", "Steinberg Media Technologies");
        MANUFACTUROR_REGISTRY.put("0x002011", "Gibson Guitar");
        MANUFACTUROR_REGISTRY.put("0x00206B", "Arturia");

        // --- Special IDs ---
        MANUFACTUROR_REGISTRY.put("0x7D", "Educational / Research Use (Non-Commercial)");
    }

    /**
     * All these message are META messages
     */
    public boolean processMessage(MetaMessage message, MidiEventInfo midiEventInfo) {
        boolean result = true;

        midiEventInfo.setRootEventKey(EventKeyType.META);

        midiEventInfo.setColor(getColor("meta"));
        midiEventInfo.setMessage(SysexToHexStringConvertor.convertToHexString(message.getMessage()));

        int type = message.getType();
        byte[] data = message.getData();

        switch (type) {
            case 0x01 -> handleText(data, midiEventInfo);
            case 0x02 -> handleCopyright(data, midiEventInfo);
            case 0x03 -> handleTrackName(data, midiEventInfo);
            case 0x04 -> handleInstrumentName(data, midiEventInfo);
            case 0x05 -> handleLyric(data, midiEventInfo);
            case 0x06 -> handleMarker(data, midiEventInfo);
            case 0x07 -> handleCuePoint(data, midiEventInfo);
            case 0x20 -> handleChannelPrefix(data, midiEventInfo);
            case 0x21 -> handleMIDIPort(data, midiEventInfo);
            case 0x2F -> result = false; // We don't want this in the events list, so KEEP this
            case 0x51 -> handleTempo(data, midiEventInfo);
            case 0x54 -> handleSMPTEOffset(data, midiEventInfo);
            case 0x58 -> handleTimeSignature(data, midiEventInfo);
            case 0x59 -> handleKeySignature(data, midiEventInfo);
            case 0x7F -> {
                handleSequencer(data, midiEventInfo);
                result = false;
            }

            default -> handleUnknowMessage(type, midiEventInfo);
        }

        return result;
    }

    /*
     * Here come the meta messages
     */
    private static void handleText(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_TEXT);

        midiEventInfo.setDescription("Text");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleCopyright(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_COPYRIGHT);

        midiEventInfo.setDescription("Copyright");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleTrackName(byte[] data, MidiEventInfo midiEventInfo) {

        midiEventInfo.setEventEventKey(META_TRACK_NAME);

        midiEventInfo.setDescription("Track Name");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleInstrumentName(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_INSTRUMENT_NAME);

        midiEventInfo.setDescription("Instrument");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleLyric(byte[] data, MidiEventInfo midiEventInfo) {

        midiEventInfo.setEventEventKey(META_LYRIC);

        midiEventInfo.setDescription("Lyric");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleMarker(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setColor(getColor("meta", "marker"));
        midiEventInfo.setEventEventKey(META_MARKER);

        midiEventInfo.setDescription("Marker");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleCuePoint(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_CUE_POINT);

        midiEventInfo.setDescription("Cue Point");
        midiEventInfo.setComment(new String(data, StandardCharsets.ISO_8859_1));
    }

    private static void handleChannelPrefix(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_CHANNEL_PREFIX);

        midiEventInfo.setDescription("Channel Prefix");

        int prefix = data[0] & 0xFF;
        midiEventInfo.setComment(SysexToHexStringConvertor.convertToHexString(new byte[]{(byte)(data[0] & 0xFF)}));
    }

    private static void handleMIDIPort(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_MIDI_PORT);

        midiEventInfo.setDescription("Midi Port");

        int portNumber = data[0] & 0xFF;

        midiEventInfo.setComment("" + portNumber);
    }

    private static void handleTempo(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setColor(getColor("meta", "tempo"));
        midiEventInfo.setEventEventKey(META_TEMPO);

        midiEventInfo.setDescription("Tempo");

        int mspq = ((data[0] & 0xFF) << 16)
                | ((data[1] & 0xFF) << 8)
                | (data[2] & 0xFF);

        double bpm = 60_000_000.0 / mspq;
        int bpmDisplay = (int) Math.round(bpm);

        midiEventInfo.setComment(String.format("%d bpm", bpmDisplay));
    }

    private static void handleSMPTEOffset(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_SMPTE_OFFSET);

        midiEventInfo.setDescription("SMPTE Offset");

        int firstByte = data[0] & 0xFF;
        int minutes   = data[1] & 0xFF;
        int seconds   = data[2] & 0xFF;
        int frames    = data[3] & 0xFF;
        int subFrames = data[4] & 0xFF;

        // Extract Frame Rate from the top 2 bits (bits 5 and 6)
        int fpsCode = (firstByte & 0x60) >> 5;
        String fpsLabel = "24 FPS";

        switch (fpsCode) {
            case 0 -> fpsLabel = "24 FPS";
            case 1 -> fpsLabel = "25 FPS";
            case 2 -> fpsLabel = "30 FPS (Drop Frame)";
            case 3 -> fpsLabel = "30 FPS (Non-Drop)";
        }

        // Extract Hours from the lower 5 bits
        int hours = firstByte & 0x1F;

        String smpte = String.format("%02d:%02d:%02d:%02d.%02d (%s)",
                hours, minutes, seconds, frames, subFrames, fpsLabel);
        midiEventInfo.setComment(smpte);
    }

    private static void handleTimeSignature(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_TIME_SIGNATURE);

        midiEventInfo.setColor(getColor("meta", "key signature"));
        midiEventInfo.setDescription("Time Signature");

        int numerator = data[0] & 0xFF;
        int denominatorExponent = data[1] & 0xFF;
        int denominator = 1 << denominatorExponent;

        midiEventInfo.setComment(String.format("%d/%d", numerator, denominator));
    }

    private static void handleKeySignature(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_KEY_SIGNATURE);

        midiEventInfo.setColor(getColor("meta", "key signature"));
        midiEventInfo.setDescription("Key Signature");

        int sharpsFlats = data[0];
        int isMinor = data[1];

        String scaleType = (isMinor == 1) ? "Minor" : "Major";
        String keyName = getMusicalKeyName(sharpsFlats, isMinor == 1);

        midiEventInfo.setComment(String.format("%s (%s)", keyName, scaleType));
    }

    private static void handleSequencer(byte[] data, MidiEventInfo midiEventInfo) {
        midiEventInfo.setEventEventKey(META_SEQUENCER);

        midiEventInfo.setDescription("Sequencer Data");

        String manufacturerId = "";

        // Determine Manufacturer ID (Standard MIDI Sysex/Meta Rules)
        if (data[0] != 0x00) {
            // 1-byte Manufacturer ID
            manufacturerId = String.format("%02X", data[0] & 0xFF);
        } else if (data.length >= 3) {
            // 3-byte Manufacturer ID (starts with 0x00)
            manufacturerId = String.format("%02X%02X%02X", ((data[0] & 0xFF) << 16), ((data[1] & 0xFF) << 8), (data[2] & 0xFF));
        } else {
            midiEventInfo.setComment("Malformed Vendor Data");
            return;
        }

        String vendorId = MANUFACTUROR_REGISTRY.getOrDefault(manufacturerId, "Unknown Manufacturer");
        midiEventInfo.setComment(vendorId);
    }

    /*
     * Here come the problem messages
     */
    private void handleUnknowMessage(int type, MidiEventInfo midiEventInfo) {
        errors++;

        midiEventInfo.setColor(getColor("meta", "unknown or error"));

        midiEventInfo.setDescription("Unknown META event");
        midiEventInfo.setComment(String.format("%02X", type));
    }

    private static String getMusicalKeyName(int sharpsFlats, boolean isMinor) {
        if (!isMinor) {
            return switch (sharpsFlats) {
                case -7 -> "C♭";
                case -6 -> "G♭";
                case -5 -> "D♭";
                case -4 -> "A♭";
                case -3 -> "E♭";
                case -2 -> "B♭";
                case -1 -> "F";
                case 0 -> "C";
                case 1 -> "G";
                case 2 -> "D";
                case 3 -> "A";
                case 4 -> "E";
                case 5 -> "B";
                case 6 -> "F#";
                case 7 -> "C#";
                default -> "Unknown";
            };
        } else {
            return switch (sharpsFlats) {
                case -7 -> "A♭";
                case -6 -> "E♭";
                case -5 -> "B♭";
                case -4 -> "F";
                case -3 -> "C";
                case -2 -> "G";
                case -1 -> "D";
                case 0 -> "A";
                case 1 -> "E";
                case 2 -> "B";
                case 3 -> "F#";
                case 4 -> "C#";
                case 5 -> "G#";
                case 6 -> "D#";
                case 7 -> "A#";
                default -> "Unknown";
            };
        }
    }
}
