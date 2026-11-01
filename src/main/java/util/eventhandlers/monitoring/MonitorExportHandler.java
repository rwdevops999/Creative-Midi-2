package util.eventhandlers.monitoring;


import entity.monitor.ColoredItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AppNotifications;
import util.eventhandlers.IEventHandler;
import util.properties.AppProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static util.properties.PropertyKeys.EXPORT_DIR;

public class MonitorExportHandler implements IEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(MonitorExportHandler.class);

    @Override
    public void handle(Object o) {
        List<ColoredItem> list = (List<ColoredItem>)o;

        String dir = AppProperties.getInstance().getPropertyAsString(EXPORT_DIR, "./data/export");
        String fileName = "monitor.txt";

        try {
            Path filePath = Paths.get(dir, fileName);
            logger.debug("Exporting monitor to file {}", filePath);
            Files.createDirectories(filePath.getParent());

            List<String> lines = list.stream().
                    filter(i -> ! i.getName().isEmpty()).
                    map(ColoredItem::getReadableText).
                    toList();

            Files.write(filePath, lines);

            AppNotifications.showNotification("Monitor", "Monitor context exported to: '" +  filePath.toFile() + "'");
        } catch (IOException ioe) {
            logger.debug("[CM_MONITOR_EXPORT_HANDLER] EXCEPTION: Error exporting monitor to file CAUSE: {}", ioe.getMessage());
        }
    }
}
