package scenes.sysex.container;

import app.CreativeApp;
import entity.sysex.Sysex;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import util.AppNotifications;
import util.properties.AppProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static util.properties.PropertyKeys.SYSEX_DIR;
import static util.properties.PropertyKeys.SYSEX_FILENAME;

public class SysexContainer {
    private static final Logger logger = LoggerFactory.getLogger(SysexContainer.class);

    @Getter
    private static SysexContainer instance = new SysexContainer();

    @Getter
    private List<Sysex> sysexList;

    private final ObjectMapper mapper;

    private SysexContainer() {
        mapper = new ObjectMapper();
        sysexList = new ArrayList<>();
    }

    public void addSysex(Sysex sysex) {
        // If we add a sysex already in the list, it replaces the sysex
        Sysex sysexInList = sysexList.stream().filter(s -> s.getName().equals(sysex.getName())).findFirst().orElse(null);
        if (sysexInList != null) {
            int index = sysexList.indexOf(sysexInList);
            sysexList.set(index, sysex);
        } else {
            sysexList.add(sysex);
        }

        export();
    }

    public void deleteSysex(Sysex sysex) {
        sysexList.remove(sysex);
        export();
    }

    public void replace(Sysex originalSysex, Sysex newSysex) {
        int index = sysexList.indexOf(originalSysex);
        sysexList.set(index, newSysex);
        export();
    }

    private void export() {
        String sysexPath = AppProperties.getInstance().getPropertyAsString(SYSEX_DIR, "./data/sysex");
        String sysexFilename = AppProperties.getInstance().getPropertyAsString(SYSEX_FILENAME, "sysex.json");

        String sysexFile = sysexPath + "/" + sysexFilename;

        ObjectMapper mapperExport = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        try {
            File file = new File(sysexFile);
            mapperExport.writeValue(file, sysexList);
            AppNotifications.showNotification("Sysex export", "sysexfile written to: " + sysexFile);
        } catch (Exception e) {
            logger.error("Exception: CAUSE: {}", e.getMessage());
        }
    }

    public void load() {
        String sysexPath = AppProperties.getInstance().getPropertyAsString(SYSEX_DIR, "./data/sysex");
        String sysexFilename = AppProperties.getInstance().getPropertyAsString(SYSEX_FILENAME, "sysex.json");

        String sysexFile = sysexPath + "/" + sysexFilename;

        try (InputStream in = Files.newInputStream(Path.of(sysexFile))) {
            sysexList = mapper.readValue(in, new TypeReference<>() {});
        } catch (IOException ioe) {
            logger.error("Exception. CAUSE: {}", ioe.getMessage());
        }
    }
}
