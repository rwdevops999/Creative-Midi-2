package util.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.device.DeviceScanner;
import util.filter.DetailFilter;
import util.midi.MBTCalculator;
import util.properties.AppProperties;

import static util.properties.PropertyKeys.TARGET_PPQ;

public class Initialize {
    private static final Logger logger = LoggerFactory.getLogger(Initialize.class);

    public Initialize() {
        initApp();
    }

    private void initApp() {

        logger.info("Initializing Application");

        // 1. Read Properties
        AppProperties.getInstance().loadAllProperties();

        // TODO Remove this a it is for testing
        DeviceScanner task = new DeviceScanner();
        Thread thread = new Thread(task);
        thread.start();

        int targetPPQ = AppProperties.getInstance().getPropertyAsInteger(TARGET_PPQ, 480);
        MBTCalculator.setTargetPPQ(targetPPQ);

        // force creation of detail filter classes
        DetailFilter.getInstance().printAlive();
    }
}
