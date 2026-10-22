package util.app;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class Logging {
    public static void setup(boolean enable) {
        if (! enable) {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            // switch root and specific loggers to OFF
            config.getLoggers().values().forEach(loggerConfig -> loggerConfig.setLevel(Level.OFF));
            context.getRootLogger().setLevel(Level.OFF);
        }
    }
}
