package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OS {
    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    public static Boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();

        return !os.contains("mac");
    }
}