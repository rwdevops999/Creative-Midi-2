package util.properties;

import javafx.stage.Stage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.app.Initialize;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppProperties {
    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    private Properties properties = new Properties();

    @Getter
    private static AppProperties instance = new AppProperties();

    Set<String> excludedDirs = Set.of(".mvn", "target");

    private AppProperties() {
    }

    public void loadAllProperties() {
        logger.info("Loading properties");
        Path rootDir = Paths.get(".");

        List<Path> propFiles = new ArrayList<>();

        try {
            Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // If the current directory name is in our exclusion set, skip it entirely
                    if (excludedDirs.contains(dir.getFileName().toString())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Check if the file ends with .properties
                    if (file.getFileName().toString().endsWith(".properties")) {
                        propFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            try {
                for (Path p : propFiles) {
                    InputStream input = new FileInputStream(p.toFile().getPath());

                    Properties tmpProperties = new Properties();
                    tmpProperties.load(input);
                    properties.putAll(tmpProperties);
                }
            } catch (IOException ioe) {
                logger.error("Exception: CAUSE {}", ioe.getMessage());
            }
        } catch (IOException ioe) {
            logger.error("Exception: CAUSE {}", ioe.getMessage());
        }
    }

    public String getPropertyAsString(String key, String defaultValue) {
        if (properties.containsKey(key)) {
            return properties.get(key).toString();
        }

        return defaultValue;
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        if (properties.containsKey(key)) {
            return Boolean.parseBoolean(properties.getProperty(key));
        }

        return defaultValue;
    }

    public Integer getPropertyAsInteger(String key, int defaultValue) {
        if (properties.containsKey(key)) {
            return Integer.parseInt(getPropertyAsString(key, ""+defaultValue));
        }

        return defaultValue;
    }
}
