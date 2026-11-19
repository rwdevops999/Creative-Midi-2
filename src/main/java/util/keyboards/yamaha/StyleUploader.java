package util.keyboards.yamaha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.Keyboard;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class StyleUploader implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(StyleUploader.class);

    private static KbCommunication kbComm = KbCommunication.getInstance();

    public void upload(Path fileOnDisk, String destinationName) throws MidiUnavailableException {
        try {
            kbComm.checkCommunication();
            kbComm.identity();
            kbComm.connect();

            Keyboard kb = kbComm.getKeyboardDrives();
            for (KbDrive drive : kb.getDrives()) {
                String sysex = SysexBuilder.getAllFilesOnDriveSysex(drive.getId());
                kbComm.retrieveData(drive, sysex);
            }

            byte[] data = Files.readAllBytes(fileOnDisk);

            byte[] setup = FileTransferBuilder.packedDestination(kb, "USER", "STYLE", destinationName, data.length);
            kbComm.send(setup);
            kbComm.waitForResponse(KbCommunication.ACK, 10000);

            List<byte[]> packets = FileTransferBuilder.buildDataPackets(data);

            System.out.println("SENDING PACKETS");
            int i = 0;
            for (byte[] packet : packets) {
                System.out.println("SENDING PACKET: " + i);
                kbComm.send(packet);
                kbComm.waitForResponse(KbCommunication.ACK, 10000);
                i++;
            }

            System.out.println("DONE");

            kbComm.clearHandshakeQueue();
            kbComm.send(new byte[] {
                    (byte) 0xF0, 0x43, 0x50, 0x00, 0x03, 0x02, (byte) 0xF7
            });

            // 2. wait for confirmation F0 43 50 00 03 03 F7
            byte[] completed = kbComm.waitForResponse(
                    new byte[] { (byte) 0xF0, 0x43, 0x50, 0x00, 0x03, 0x03, (byte) 0xF7 },
                    30000
            );

            if (completed == null) {
                throw new IOException("Transfer niet bevestigd door keyboard (TIMEOUT op 03 03)");
            }

            // Close the transfer-mode clearing the keyboard-screen again
            kbComm.disconnect();
        } catch (Exception e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
    }
}
