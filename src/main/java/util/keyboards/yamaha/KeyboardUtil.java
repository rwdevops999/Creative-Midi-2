package util.keyboards.yamaha;

import org.apache.commons.io.FilenameUtils;
import util.keyboards.comm.KbCommunication;
import util.keyboards.comm.SysexBuilder;
import util.keyboards.yamaha.entity.KbDrive;
import util.keyboards.yamaha.entity.KbFile;
import util.keyboards.yamaha.entity.KbFolder;
import util.keyboards.yamaha.entity.Keyboard;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.keyboards.comm.KbUtil.getFilesFrom;
import static util.keyboards.yamaha.General.starts;

public class KeyboardUtil {
    private final KbCommunication kbComm = KbCommunication.getInstance();

    public synchronized List<String> getStyleFiles() throws Exception {
        List<String> files;

        if (!kbComm.isInitialised()) {
            return new ArrayList<>();
        }

        kbComm.testCommunication();

        try {
            kbComm.identity();
            kbComm.connect();

            Keyboard kb = kbComm.getKeyboardSetup();

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

        kbComm.testCommunication();

        try {
            kbComm.identity();
            kbComm.connect();

            Keyboard kb = kbComm.getKeyboardSetup();

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
}
