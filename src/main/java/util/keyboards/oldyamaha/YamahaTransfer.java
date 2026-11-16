package util.keyboards.oldyamaha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.sysex.SysexWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

public final class YamahaTransfer {
    private static final Logger logger = LoggerFactory.getLogger(YamahaTransfer.class);

    private final Receiver receiver;

    SysexWriter sysexWriter;
    YamahaTransfer(Receiver receiver) {
        this.receiver = receiver;
        this.sysexWriter = SysexWriter.getInstance();
    }

    private static String toHex(byte[] data) {

        StringBuilder sb =
                new StringBuilder();

        for (byte b : data) {
            sb.append(
                    String.format(
                            "%02X ",
                            b & 0xFF
                    )
            );
        }

        return sb.toString().trim();
    }

    public void sendSysex(byte[] message) throws InvalidMidiDataException {
        SysexMessage sysex = new SysexMessage();

        sysex.setMessage(
                SysexMessage.SYSTEM_EXCLUSIVE,
                message,
                message.length
        );

        receiver.send(sysex, -1);

        logger.debug("SEND TO PSR: {}", toHex(message));
    }
}
