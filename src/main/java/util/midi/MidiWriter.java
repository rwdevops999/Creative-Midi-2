package util.midi;

import com.sun.jdi.ByteType;
import entity.device.DeviceInfo;
import entity.midi.Midi;
import lombok.Getter;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.registry.CommunicationMessage;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MidiWriter {
    @Getter
    private static MidiWriter instance = new MidiWriter();

    private MidiWriter() {
    }

    public void send(Midi midi) throws MidiUnavailableException {
        DeviceInfo dev = AppInfo.INSTANCE.getSelectedDevice();

        MidiDevice midiInputDevice = dev.getMidiInputDevice();
        if (midiInputDevice != null) {
            midiInputDevice.getReceiver().send(midi.getMessage(), -1);
            CommunicationMessage.getInstance().showMessage(CommunicationType.OUTBOUND, midi.toString());
        }
    }

    List<Midi> midisList = new ArrayList<>();

    public void reset() {
        midisList = new ArrayList<>();

        try {
            Midi midi;

            // Pitch Wheel
            midi = new Midi("pitch wheel", 0, 0xE0, 0x0, 0x40);
            distributeOverAllChannels(midi);

            // Modulation Wheel
            midi = new Midi("modulation wheel", 0, 0xB0, 0x01, 0x00);
            distributeOverAllChannels(midi);

            // Volume
            midi = new Midi("volume", 0, 0xB0, 0x07, 0x7F);
            distributeOverAllChannels(midi);

            // Panning
            midi = new Midi("panning", 0, 0xB0, 0x0A, 0x40);
            distributeOverAllChannels(midi);

            // Sustain Pedal
            midi = new Midi("sustain pedal", 0, 0xB0, 0x40, 0x00);
            distributeOverAllChannels(midi);

            // Sustenuto
            midi = new Midi("sustenuto", 0, 0xB0, 0x42, 0x00);
            distributeOverAllChannels(midi);

            // Soft Pedal
            midi = new Midi("soft pedal", 0, 0xB0, 0x43, 0x00);
            distributeOverAllChannels(midi);

            // All Controllers Off
            midi = new Midi("all controllers off", 0, 0xB0, 0x79, 0x00);
            distributeOverAllChannels(midi);

            // All Notes Off
            midi = new Midi("all notes off", 0, 0xB0, 0x7B, 0x00);
            distributeOverAllChannels(midi);

            // All Sound Off
            midi = new Midi("all sound off", 0, 0xB0, 0x78, 0x00);
            distributeOverAllChannels(midi);

            // Note Offs
//            midi = new Midi("note off using note on (velocity 0)", 0, 0x90, 0x00, 0x00);
//            distributeOverAllNotes(midi);

            sendBulk();
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void distributeOverAllChannels(Midi midi) throws InvalidMidiDataException {
        // Haal de pure basis status op (bijv. 0xB0 of 0xE0) door het oude kanaal eraf te strippen
        int baseStatus = midi.getStatusByte() & 0xF0;

        for (int c = 0; c < 16; c++) {
            // Bereken de exacte statusbyte voor DIT kanaal (bijv. 0xB0, 0xB1 ... t/m 0xBF)
            int statusForChannel = baseStatus | (c & 0x0F);

            // Maak een 100% nieuw, onafhankelijk Midi object aan voor dit kanaal
            Midi channelMidi = new Midi(
                    midi.getName(),
                    c, // Het kanaal-nummer (0-15)
                    statusForChannel,
                    midi.getRawData()[1], // byte 1 (bijv. de controller zoals 0x40)
                    midi.getRawData()[2]  // byte 2 (bijv. de waarde zoals 0x00)
            );

            midisList.add(channelMidi);
        }
    }

/*    private void distributeOverAllNotes(Midi midi) throws InvalidMidiDataException {
        for (int i = 0; i < 128; i++) {
            Midi dataMidi = new Midi(midi, (byte)i);
            midisList.add(dataMidi);
        }
    }
*/
    public void testTone() {
        midisList = new ArrayList<>();

        try {
            Midi midi = new Midi("Bank Select MSB", 0, 0xB0, 0X00, 0x00);
            midisList.add(midi);

            midi = new Midi("Bank Select LSB", 0, 0xB0, 0X20, 0x00);
            midisList.add(midi);

            midi = new Midi("Program Change", 0, 0xC0, 0X2C);
            midisList.add(midi);

            midi = new Midi("Note On", 0, 0x90, 0X3C, 0x64);
            midisList.add(midi);

            sendBulk();
            Thread.sleep(100);
        } catch (InvalidMidiDataException | MidiUnavailableException| InterruptedException e) {
            e.printStackTrace();
        }
    }

        private void sendBulk() throws MidiUnavailableException {
        for (Midi midi : midisList) {
            send(midi);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
