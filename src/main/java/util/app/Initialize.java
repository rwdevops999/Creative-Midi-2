package util.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Theme;
import util.device.DeviceScanner;
import util.filter.DetailFilter;
import util.midi.MBTCalculator;
import util.properties.AppProperties;

import static util.properties.PropertyKeys.APP_THEME;
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

        // 2. Startup the Device Scanner task
        DeviceScanner task = new DeviceScanner();
        Thread thread = new Thread(task);
        thread.start();

        // 3. Set the PPQ from the propeties file
        int targetPPQ = AppProperties.getInstance().getPropertyAsInteger(TARGET_PPQ, 480);
        MBTCalculator.setTargetPPQ(targetPPQ);

        // 4. force creation of detail filter classes
        DetailFilter.getInstance().printAlive();

        // 5. Setup theme
        String theme = AppProperties.getInstance().getPropertyAsString(APP_THEME, "dark").toUpperCase();
        Theme.getInstance().setAppTheme(Theme.ThemeType.valueOf(theme));
        Theme.getInstance().switchTheme(AppInfo.INSTANCE.getRootScene());

    }
}
