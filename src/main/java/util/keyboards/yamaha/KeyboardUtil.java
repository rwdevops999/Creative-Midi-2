package util.keyboards.yamaha;

import fx.dialog.DialogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.Keyboard;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

import static util.keyboards.yamaha.KbUtil.getFilesFrom;

public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);

    private final KbCommunication kbComm = KbCommunication.getInstance();

    public synchronized List<String> getStyleFiles() throws Exception {
        List<String> files;

        if (!kbComm.isInitialised()) {
            return new ArrayList<>();
        }

        kbComm.checkCommunication();

        try {
            kbComm.identity();
            kbComm.connect();

            Keyboard kb = kbComm.getKeyboardDrives();

            for (KbDrive drive : kb.getDrives()) {
                String sysex = SysexBuilder.getAllFilesOnDriveSysex(drive.getId());
                kbComm.retrieveData(drive, sysex);
            }

            files = getFilesFrom(kb, "USER", "STYLE", kbComm);
        } finally {
            try {
                kbComm.disconnect();
            } catch (Exception ignored) {
            }
        }

        return files;
    }

    public synchronized List<String> getSongFiles() throws Exception {
        List<String> files;

        if (!kbComm.isInitialised()) {
            return new ArrayList<>();
        }

        kbComm.checkCommunication();

        try {
            kbComm.identity();
            kbComm.connect();

            Keyboard kb = kbComm.getKeyboardDrives();

            for (KbDrive drive : kb.getDrives()) {
                String sysex = SysexBuilder.getAllFilesOnDriveSysex(drive.getId());
                kbComm.retrieveData(drive, sysex);
            }

            files = getFilesFrom(kb, "USER", "SONG", kbComm);
        } finally {
            try {
                kbComm.disconnect();
            } catch (Exception ignored) {
            }
        }

        return files;
    }

    public synchronized void uploadStyle(String styleFileOnDisk, String styleName, BiConsumer<Integer, Integer> progressCallback) {
        if (!kbComm.isInitialised()) {
            DialogFactory.getInstance().renderErrorDialog("No device selected");
        } else {
            try (StyleUploader styleUploader = new StyleUploader()) {
                File f = new File(styleFileOnDisk);
                String newStyleName = styleName + ".sty";

                styleUploader.upload(f.toPath(), newStyleName, progressCallback);
                logger.debug("Uploaded style file on disk: " + styleFileOnDisk);
            } catch (Exception e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }
        }
    }

    public synchronized void uploadSong(String midiFileOnDisk, String songName, BiConsumer<Integer, Integer> progressCallback) {
        if (!kbComm.isInitialised()) {
            DialogFactory.getInstance().renderErrorDialog("No device selected");
        } else {
            try (MidiUploader midiUploader = new MidiUploader()) {
                File f = new File(midiFileOnDisk);
                String newSongName = songName + ".mid";

                midiUploader.upload(f.toPath(), newSongName, progressCallback);
                logger.debug("Uploaded midi file on disk: " + midiFileOnDisk);
            } catch (Exception e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }
        }
    }
}
