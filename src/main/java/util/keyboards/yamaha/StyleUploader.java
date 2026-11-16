package util.keyboards.yamaha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.Keyboard;
import util.sysex.SysexWriter;

import javax.sound.midi.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static util.keyboards.yamaha.General.asHex;

public class StyleUploader implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(StyleUploader.class);
    private static KbCommunication kbComm = KbCommunication.getInstance();

    private SysexWriter sysexWriter = SysexWriter.getInstance();

    public StyleUploader() {
    }

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
            List<byte[]> packets = FilePackets.buildDataPackets(data);
            byte[] setup = FilePackets.buildTransferRequest(kb, "USER", "STYLE", destinationName, data.length);

            byte[] storageResponse = kbComm.sendMessageAndWait(
                    "F0 43 50 00 05 06 00 01 F7",
                    2000, true
            );

            logger.debug("Storage response: {}", (storageResponse == null ? "TIMEOUT" : asHex(storageResponse)));

            kbComm.clearHandshakeQueue();
            kbComm.send(setup);
            kbComm.waitForResponse(new byte[] {(byte)0xF0,0x43,0x50,0x00,0x03,0x00,(byte)0xF7}, 10000);
            for (int i = 0; i < packets.size(); i++) {
                kbComm.clearHandshakeQueue();
                kbComm.send(packets.get(i));
                byte[] ack = kbComm.waitForResponse(new byte[] {
                                (byte) 0xF0, 0x43, 0x50, 0x00,
                                0x03, 0x00, (byte) 0xF7
                        }, 10000
                );

                if (ack == null) {
                    throw new IOException(
                            "No ACK for style packet " +
                                    (i + 1) + "/" + packets.size()
                    );
                }

                logger.debug("Style upload: {}/{} packets%n",i + 1, packets.size());
            }

            kbComm.clearHandshakeQueue();
            kbComm.send(new byte[] {
                    (byte) 0xF0, 0x43, 0x50, 0x00,
                    0x03, 0x02, (byte) 0xF7
            });

            // Wait for the actual transfer-completion response.
            // Ignore intermediate 00 05 02 notifications.
            byte[] completed = kbComm.waitForResponse(new byte[] {
                            (byte) 0xF0, 0x43, 0x50, 0x00,
                            0x03, 0x03
                    }, 30000
            );
            logger.debug("Transfer completed: {}",  asHex(completed));

            System.out.println("CHECKIE");
        } catch (Exception e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
        finally {
            try {
                kbComm.disconnect();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void close() throws Exception {
        // close communication
        kbComm.close();
    }
}
