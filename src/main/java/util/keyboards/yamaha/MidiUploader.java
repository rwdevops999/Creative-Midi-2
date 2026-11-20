package util.keyboards.yamaha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.Keyboard;
import util.sysex.SysexWriter;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

import static util.keyboards.yamaha.General.asHex;

public class MidiUploader implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MidiUploader.class);
    private static KbCommunication kbComm = KbCommunication.getInstance();

    public MidiUploader() {
    }

    public void upload(Path fileOnDisk, String destinationName, BiConsumer<Integer, Integer> progressCallback) throws MidiUnavailableException {
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

            byte[] setup = FileTransferBuilder.packedDestination(kb, "USER", "SONG", destinationName, data.length);
            kbComm.send(setup);
            kbComm.waitForResponse(KbCommunication.ACK, 10000);

            List<byte[]> packets = FileTransferBuilder.buildDataPackets(data);

            int i = 0;
            for (byte[] packet : packets) {
                kbComm.send(packet);
                kbComm.waitForResponse(KbCommunication.ACK, 10000);
                i++;

                if (progressCallback != null) {
                    progressCallback.accept(i, packets.size());
                }
            }

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
                throw new IOException("Transfer not confirmed by the keyboard (TIMEOUT on 03 03)");
            }

            // Close the transfer-mode clearing the keyboard-screen again
            kbComm.disconnect();
        } catch (Exception e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        // close communication
        kbComm.close();
    }
}
