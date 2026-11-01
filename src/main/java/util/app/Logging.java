package util.app;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class Logging {
    public static void setup(boolean enable) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        if (!enable) {
            // Switch root and specific loggers to OFF
            config.getLoggers().values().forEach(loggerConfig -> loggerConfig.setLevel(Level.OFF));
            context.getRootLogger().setLevel(Level.OFF);
        } else {
            // Optional: If you ever want to force them back to their XML defaults or a specific level programmatically
            config.getLoggers().values().forEach(loggerConfig -> loggerConfig.setLevel(Level.DEBUG));
            context.getRootLogger().setLevel(Level.DEBUG);
        }

        // CRUCIAL: Tell Log4j2 to apply the changes you just made in memory
        context.updateLoggers();
    }}
