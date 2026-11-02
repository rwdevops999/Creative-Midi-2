package util.sysex;

import entity.device.DeviceInfo;
import entity.midi.Midi;
import entity.sysex.Sysex;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.midi.MidiWriter;
import util.registry.CommunicationMessage;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.SysexMessage;
import java.io.Writer;

public class SysexWriter {
    private static final Logger logger = LoggerFactory.getLogger(SysexWriter.class);

    @Getter
    private static final SysexWriter instance = new SysexWriter();

    private SysexWriter() {
    }

    public void send(Sysex sysex) throws InvalidMidiDataException, MidiUnavailableException {
        DeviceInfo dev = AppInfo.INSTANCE.getSelectedDevice();

        if (dev != null) {
            MidiDevice midiInputDevice = dev.getMidiInputDevice();

            SysexMessage message = new SysexMessage();
            String content = sysex.getContent().getSysex();

            byte[] data = sysex.getContent().getSysex().getBytes();
            message.setMessage(data, data.length);

            midiInputDevice.getReceiver().send(message, -1);

            CommunicationMessage.getInstance().showMessage(CommunicationType.OUTBOUND, content);
        }
    }
}
