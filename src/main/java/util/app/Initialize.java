package util.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initialize {
    private static final Logger logger = LoggerFactory.getLogger(Initialize.class);

    public Initialize() {
        initApp();
    }

    private void initApp() {
        logger.info("Initializing Application");
    }
}
