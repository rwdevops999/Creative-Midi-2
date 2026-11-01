package util.device;

import entity.device.DeviceInfo;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiException;
import util.OS;
import util.app.AppInfo;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.List;

import static util.AnsiColors.*;

public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Getter
    private final static DeviceService instance = new DeviceService();

    private DeviceService() {
    }

    private List<DeviceInfo> devices;
    public List<DeviceInfo> scan() {
        devices = new ArrayList<>();

        CoreMidiDeviceProvider provider = null;

        try {
            MidiDevice.Info[] infos;
            if (OS.isWindows()) {
                infos = MidiSystem.getMidiDeviceInfo();
            } else {
                provider = new CoreMidiDeviceProvider();
                infos = provider.getDeviceInfo();
            }
            for (MidiDevice.Info info : infos) {
                String name = info.getName();

                if (name.contains("Microsoft GS Wavetable Synth") ||
                        name.contains("Microsoft MIDI Mapper") ||
                        name.contains("Real Time Sequencer") ||
                        name.equalsIgnoreCase("Gervill")) {

                    continue; // Continue immediate with the next device from the list
                }

                MidiDevice dev;
                if (! OS.isWindows()) {
                    dev = provider != null ? provider.getDevice(info) : null;
                } else {
                    dev = MidiSystem.getMidiDevice(info);
                }

                if (dev != null) {
                    if (AppInfo.INSTANCE.isDebugMode()) {
                        printDebugInfoOfDevice(dev);
                    }

                    if (dev.getMaxTransmitters() != 0) {
                        addDevice(dev.getDeviceInfo().getName(), dev, DeviceDirection.OUTPUT);
                    }

                    if (dev.getMaxReceivers() != 0) {
                        addDevice(dev.getDeviceInfo().getName(), dev, DeviceDirection.INPUT);
                    }
                }
            }
        } catch (CoreMidiException | MidiUnavailableException e) {
            logger.error("Exception.CAUSE {}", e.getMessage());
        }

        return devices;
    }

    private synchronized void printDebugInfoOfDevice(MidiDevice dev) {
        System.out.println("-= > Device: " + ANSI_RED + dev.getDeviceInfo().getName() + ANSI_RESET);
        System.out.println("     Vendor: " + ANSI_GREEN + dev.getDeviceInfo().getVendor() + ANSI_RESET);
        System.out.println("     num TX: " + ANSI_BLUE + dev.getMaxTransmitters() + ANSI_RESET);
        System.out.println("     num RX: " + ANSI_BLUE + dev.getMaxReceivers() + ANSI_RESET);
    }
    private void addDevice(String deviceName, MidiDevice dev, DeviceDirection direction) {
        DeviceInfo devInfo = devices.stream().filter(d -> d.getName().equals(deviceName)).findFirst().orElse(null);
        if (devInfo == null) {
            devInfo = new DeviceInfo();
            devInfo.setName(deviceName);
        } else {
            devices.remove(devInfo);
        }

        if (devInfo.getDeviceDirection() == DeviceDirection.INPUT || devInfo.getDeviceDirection() == DeviceDirection.OUTPUT) {
            devInfo.setDeviceDirection(DeviceDirection.BIDIRECTIONAL);
        } else {
            devInfo.setDeviceDirection(direction);
        }

        if (direction == DeviceDirection.INPUT) {
            devInfo.setMidiInputDevice(dev);
        } else if (direction == DeviceDirection.OUTPUT) {
            devInfo.setMidiOutputDevice(dev);
        }

        devices.add(devInfo);
    }
}
