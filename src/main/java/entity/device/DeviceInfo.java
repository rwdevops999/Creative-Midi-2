package entity.device;

import entity.NameEntity;
import lombok.Getter;
import util.device.DeviceDirection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

public class DeviceInfo extends NameEntity {
    @Getter
    private DeviceDirection deviceDirection;

    @Getter
    private MidiDevice midiInputDevice;

    public void setMidiInputDevice(MidiDevice midiDevice) {
        midiInputDevice = midiDevice;

        if (midiInputDevice != null) {
            if (! midiInputDevice.isOpen()) {
                try {
                    midiInputDevice.open();
                } catch (MidiUnavailableException e) {
                    try {
                        midiInputDevice.close();
                    } catch (Exception closeEx) {
                        // ignore, just to free resources
                    }

                    midiInputDevice = null;
                }
            }
        }
    }

    @Getter
    private MidiDevice midiOutputDevice;

    public void setMidiOutputDevice(MidiDevice midiDevice) {
        midiOutputDevice = midiDevice;

        if (midiDevice != null) {
            if (! midiOutputDevice.isOpen()) {
                try {
                    midiOutputDevice.open();
                } catch (MidiUnavailableException e) {
                    try {
                        midiOutputDevice.close();
                    } catch (Exception closeEx) {
                        // ignore, just free the resources
                    }
                    midiOutputDevice = null;
                }
            }
        }
    }

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
