package entity.midi;

import entity.NameEntity;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class Midi extends NameEntity {
    private static final Logger logger = LoggerFactory.getLogger(Midi.class);

    @Getter
    private Integer[] rawData;
    @Getter
    private ShortMessage message;

    public Midi() {
        super();
    }

    public Midi(String name) {
        this.name = name;
    }

    public Midi(String name, Integer channel, Integer[] bytes) throws InvalidMidiDataException {
        this.name = name;

        this.rawData = new Integer[3];
        for (int i = 0; i < bytes.length; i++) {
            rawData[i] = bytes[i];
            if (i == 0 && channel != null) {
                rawData[i] = rawData[i] | (channel.byteValue() & 0xF);
            }
        }

        createMessage(rawData[0], rawData[1], rawData[2]);
    }

    public Midi(String name, Integer channel, int ...bytes) throws InvalidMidiDataException {
        this.name = name;

        this.rawData = new Integer[3];
        for (int i = 0; i < bytes.length; i++) {
            rawData[i] = bytes[i];
            if (i == 0 && channel != null) {
                rawData[i] = rawData[i] | (channel.byteValue() & 0xF);
            }
        }

        createMessage(rawData[0], rawData[1], rawData[2]);
    }

    public Midi(Midi midi, int channel) throws InvalidMidiDataException {
        this(midi.getName(), channel, midi.rawData);
    }

    public Midi(Midi midi, byte b) throws InvalidMidiDataException {
        this.name = midi.name;

        this.rawData = new Integer[3];
        this.rawData[0] = midi.rawData[0];
        this.rawData[1] = b & 0xFF;
        this.rawData[2] = midi.rawData[2];

        createMessage(rawData[0], rawData[1], rawData[2]);
    }

    public int getStatusByte() {
        return (rawData.length > 0) ? (rawData[0] & 0xFF) : 0;
    }

    @Override
    public String toString() {
        if (rawData == null || rawData.length == 0) {
            return "";
        }

        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < rawData.length; i++) {
            // Format as 2-digit uppercase hex, padding with a leading zero if needed
            hexString.append(String.format("%02X", rawData[i]));

            // Add a space between bytes, but not after the last byte
            if (i < rawData.length - 1) {
                hexString.append(" ");
            }
        }
        return hexString.toString();
    }

    public void createMessage(Integer byte0, Integer byte1, Integer byte2) {
        // 1. Safety check: without status byte (byte0) we can't parse the message
        if (byte0 == null) {
            logger.error("Error: status byte (byte0) can't be null.");
            return;
        }

        // 2. Convert null-values safe to 0 and filter possible negative values out
        int b0 = byte0 & 0xFF;
        int b1 = (byte1 != null) ? (byte1 & 0xFF) : 0;
        int b2 = (byte2 != null) ? (byte2 & 0xFF) : 0;

        // 3. Isolate the message type (upper 4 bits)
        int messageType = b0 & 0xF0;

        switch (messageType) {
            // 3 bytes used (b1 en b2 are send also, even if they are null/0)
            case 0x80: // Note Off
            case 0x90: // Note On
            case 0xA0: // Polyphonic Key Pressure
            case 0xB0: // Control Change (CC)
            case 0xE0: // Pitch Bend Change
                message = createThreeByteMessage(b0, b1, b2);
                break;

            // 2 bytes used (b2 is ignored, it doesn't matter if it is null or not)
            case 0xC0: // Program Change
            case 0xD0: // Channel Pressure (Aftertouch)
                message = createTwoByteMessage(b0, b1);
                break;

            // System / Realtime messages
            case 0xF0:
                message = createSystemMessage(b0, b1, b2);
                break;

            default:
                // Invalid or unsupported status byte
                break;
        }
    }

    private ShortMessage createThreeByteMessage(int b0, int b1, int b2) {
        try {
            ShortMessage msg = new ShortMessage();

            int command = b0 & 0xF0;  // Isolate the command (e.g. 0x90 of 0x80)
            int channel = b0 & 0x0F;  // Isolate the MIDI-channel (0 t/m 15)

            // Use the official Java method that separates command and channel
            msg.setMessage(command, channel, b1, b2);
            return msg;
        } catch (InvalidMidiDataException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }

        return null;
    }

    private ShortMessage createTwoByteMessage(int b0, int b1) {
        try {
            ShortMessage msg = new ShortMessage();
            // For 2 byte messages (such as Program Change) put the third argument on 0.
            // The Java MIDI driver knows that this type bericht sends only 2 bytes.
            msg.setMessage(b0, b1, 0);
            return msg;
        } catch (InvalidMidiDataException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }

        return null;
    }

    private ShortMessage createSystemMessage(int b0, int b1, int b2) {
        try {
            ShortMessage msg = new ShortMessage();

            switch (b0) {
                case 0xF2: // Song Position Pointer (3 bytes)
                    msg.setMessage(b0, b1, b2);
                    break;

                case 0xF3: // Song Select (2 bytes)
                    msg.setMessage(b0, b1, 0);
                    break;

                // Realtime messages (1 byte long)
                case 0xF8: // MIDI Clock
                case 0xFA: // MIDI Start
                case 0xFB: // MIDI Continue
                case 0xFC: // MIDI Stop
                case 0xFE: // Active Sensing
                case 0xFF: // System Reset
                    msg.setMessage(b0, 0, 0);
                    break;

                case 0xF0: // SysEx Start
                    // SysEx is variable and uses another class (SysexMessage).
                    break;
            }

            return msg;
        } catch (InvalidMidiDataException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }

        return null;
    }
}
