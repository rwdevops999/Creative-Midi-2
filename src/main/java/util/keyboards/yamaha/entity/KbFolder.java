package util.keyboards.yamaha.entity;

/**
 * Contains folder information
 * - the folder name (SONG, STYLE, ...)
 * - a list of folders
 * - a list of files
 */
public class KbFolder extends DriveEntity {
    public KbFolder(String name) {
        this.name = name;
    }
}
