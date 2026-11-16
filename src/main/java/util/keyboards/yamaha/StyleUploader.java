package util.keyboards.yamaha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.keyboards.oldyamaha.YamahaTransfer;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.Keyboard;
import util.sysex.SysexWriter;

import javax.sound.midi.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static util.keyboards.yamaha.General.asHex;

public class StyleUploader implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(StyleUploader.class);
    private static KbCommunication kbComm = KbCommunication.getInstance();

    private SysexWriter sysexWriter = SysexWriter.getInstance();

    public StyleUploader() {
    }

    public void upload(Path fileOnDisk, String newStyleFile) throws MidiUnavailableException {
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
