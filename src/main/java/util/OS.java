package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Constants.OS_MAC;
import static util.Constants.OS_WINDOWS;

public class OS {
    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    public static int getOsCode() {
        logger.debug ("[CM_OS] Getting operating system");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            logger.debug ("[CM_OS] It's a MAC");
            return OS_MAC;
        }

        logger.debug ("[CM_OS] It's WINDOW");
        return OS_WINDOWS;
    }

    public static Boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();

        return !os.contains("mac");
    }
}