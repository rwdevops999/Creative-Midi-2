package util.keyboards.yamaha.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains keyboard internal information
 * - a list of available drives
 */

public class Keyboard {
    @Getter
    private List<KbDrive> drives;

    @Setter
    private boolean hasUsbDrive;

    public Keyboard() {
        hasUsbDrive = false;
        drives = new ArrayList<>();
    }

    public boolean hasUsbDrive() {
        return hasUsbDrive;
    }
}
