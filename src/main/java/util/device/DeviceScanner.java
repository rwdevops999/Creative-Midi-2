package util.device;

import entity.device.DeviceInfo;
import javafx.concurrent.Task;
import util.properties.AppProperties;
import util.registry.AppEvent;
import util.registry.Registry;

import java.util.List;

import static util.properties.PropertyKeys.APP_DEVICE_SCANNER_TIME;

public class DeviceScanner extends Task<Void> {

    @Override
    protected Void call() throws Exception {
        Registry registry = Registry.getInstance();
        int sleepTime  = AppProperties.getInstance().getPropertyAsInteger(APP_DEVICE_SCANNER_TIME, 0);
        while (!isCancelled()) {
            try {
                List<DeviceInfo> devices = DeviceService.getInstance().scan();

                AppEvent event = new AppEvent(AppEvent.AppEventType.DEVICE, devices);
                registry.post(event);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime * 1000L);
                }
            } catch (InterruptedException interrupted) {
                break; // Exit the loop gracefully if interrupted while sleeping
            }
        }

        return null;
    }
}
