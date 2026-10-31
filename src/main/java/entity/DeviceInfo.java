package entity;

import lombok.Getter;
import lombok.Setter;
import util.device.DeviceDirection;

import javax.sound.midi.MidiDevice;

public class DeviceInfo extends NameEntity {
    @Getter
    private DeviceDirection deviceDirection;

    @Setter
    private MidiDevice midiInputDevice;

    @Setter
    private MidiDevice midiOutputDevice;

    public DeviceInfo() {
        this.name = "";
        this.textIcon = "";
        this.deviceDirection = null;
    }

    public void setDeviceDirection(DeviceDirection deviceDirection) {
        this.deviceDirection = deviceDirection;
        if (deviceDirection.equals(DeviceDirection.INPUT)) {
            this.textIcon = "➡ ";
        } else if (deviceDirection.equals(DeviceDirection.OUTPUT)) {
            this.textIcon = "⬅ ";
        } else if (deviceDirection.equals(DeviceDirection.BIDIRECTIONAL)) {
            this.textIcon = "⇄ ";
        }
    }
}
