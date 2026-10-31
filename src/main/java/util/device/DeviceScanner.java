package util.device;

import entity.DeviceInfo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import util.properties.AppProperties;

import java.util.List;
import java.util.Properties;

import static util.properties.PropertyKeys.APP_DEVICE_SCANNER_TIME;

public class DeviceScanner extends Task<Void> {

    @Override
    protected Void call() throws Exception {
        int sleepTime  = AppProperties.getInstance().getPropertyAsInteger(APP_DEVICE_SCANNER_TIME, 0);
        while (!isCancelled()) {
            try {
                List<DeviceInfo> devices = DeviceService.getInstance().scan();

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
