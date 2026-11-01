package entity.midi;

import entity.NameEntity;
import lombok.Getter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class Midi extends NameEntity {
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
        // 1. Veiligheidscheck: Zonder status byte (byte0) kunnen we het bericht niet parseren
        if (byte0 == null) {
            System.err.println("Fout: Status byte (byte0) mag niet null zijn.");
            return;
        }

        // 2. Zet null-waarden veilig om naar 0 en filter eventuele negatieve waarden eruit
        int b0 = byte0 & 0xFF;
        int b1 = (byte1 != null) ? (byte1 & 0xFF) : 0;
        int b2 = (byte2 != null) ? (byte2 & 0xFF) : 0;

        // 3. Isoleer het berichttype (bovenste 4 bits)
        int berichtType = b0 & 0xF0;

        switch (berichtType) {
            // 3 Bytes lang (b1 en b2 worden meegestuurd, zelfs als ze null/0 waren)
            case 0x80: // Note Off
            case 0x90: // Note On
            case 0xA0: // Polyphonic Key Pressure
            case 0xB0: // Control Change (CC)
            case 0xE0: // Pitch Bend Change
                message = stuurDrieByteBericht(b0, b1, b2);
                break;

            // 2 Bytes lang (b2 wordt volledig genegeerd, dus of het nu null was maakt niet uit)
            case 0xC0: // Program Change
            case 0xD0: // Channel Pressure (Aftertouch)
                message = stuurTweeByteBericht(b0, b1);
                break;

            // System / Realtime berichten
            case 0xF0:
                message = verwerkSystemBericht(b0, b1, b2);
                break;

            default:
                // Ongeldig of niet-ondersteund status byte
                break;
        }
    }

    private ShortMessage stuurDrieByteBericht(int b0, int b1, int b2) {
        try {
            ShortMessage msg = new ShortMessage();

            int command = b0 & 0xF0;  // Isoleer het commando (bijv. 0x90 of 0x80)
            int channel = b0 & 0x0F;  // Isoleer het MIDI-kanaal (0 t/m 15)

            // Gebruik de officiële Java-methode die command en channel los scheidt
            msg.setMessage(command, channel, b1, b2);
            return msg;
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ShortMessage stuurTweeByteBericht(int b0, int b1) {
        try {
            ShortMessage msg = new ShortMessage();
            // Voor 2-byte berichten (zoals Program Change) zetten we het derde argument op 0.
            // De Java MIDI-driver weet dat dit type bericht maar 2 bytes verzendt.
            msg.setMessage(b0, b1, 0);
            return msg;
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ShortMessage verwerkSystemBericht(int b0, int b1, int b2) {
        try {
            ShortMessage msg = new ShortMessage();

            switch (b0) {
                case 0xF2: // Song Position Pointer (3 bytes)
                    msg.setMessage(b0, b1, b2);
                    break;

                case 0xF3: // Song Select (2 bytes)
                    msg.setMessage(b0, b1, 0);
                    break;

                // Realtime berichten (1 byte lang)
                case 0xF8: // MIDI Clock
                case 0xFA: // MIDI Start
                case 0xFB: // MIDI Continue
                case 0xFC: // MIDI Stop
                case 0xFE: // Active Sensing
                case 0xFF: // System Reset
                    msg.setMessage(b0, 0, 0);
                    break;

                case 0xF0: // SysEx Start
                    // SysEx is variabel en gebruikt een andere klasse (SysexMessage).
                    // Indien nodig moet dit apart opgevangen worden.
                    break;
            }

            return msg;
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return null;
    }
}
