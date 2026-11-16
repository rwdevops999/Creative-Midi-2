package util.keyboards.yamaha.entity;

/**
 * Contains
 * - the drive name (USB1 or USER)
 * - the drive id
 * - a list of folders
 * - a list of files
 */
public class KbDrive extends DriveEntity {
    public KbDrive(int id, String name) {
        super();

        this.id = id;
        this.name = name;
    }
}
