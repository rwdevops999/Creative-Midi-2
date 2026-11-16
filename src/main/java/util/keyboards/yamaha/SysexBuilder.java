package util.keyboards.yamaha;

import util.keyboards.yamaha.entity.KbFolder;

import static util.keyboards.yamaha.KbUtil.buildDirRequestSysEx;
import static util.keyboards.yamaha.KbUtil.byteToHex;

public class SysexBuilder {
    private static final String EOS = " F7";

    public static String getDrive(int id) {
        String sysexStart = "F0 43 50 00 05 0B 00 ";
        String driveNr = byteToHex((byte)id);

        return (sysexStart + driveNr + EOS);
    }

    /**
     *
     * @param id in range [0,9]
     * @return
     */
    public static String getAllFilesOnDriveSysex(int id) {
        String sysexStart = "F0 43 50 00 05 04 00 3F 00 08 00 ";
        String drive = "3" + id + " ";
        String allFiles = "3A 5C 2A 2E 2A 00";

        return sysexStart + drive + allFiles + EOS;
    }

    public static String getFilesFromFolderSysex(KbFolder folder, int driveId) {
        byte[] sysexStart = {
                (byte) 0xF0, 0x43, 0x50, 0x00, 0x05, 0x04, 0x00, 0x3F
        };
        byte[] bytes = buildDirRequestSysEx(sysexStart, driveId + ":\\" + folder.getName() + "\\*.*");

        return KbUtil.toHexString(bytes);
    }
}
