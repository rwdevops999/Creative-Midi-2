package scenes.sysex.container;

import app.CreativeApp;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import entity.sysex.Sysex;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AppNotifications;
import util.properties.AppProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // INGEBOUWD & VEILIG: Verwijdert alle nulls en lege strings ("") globally!
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY);

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
            sysexList = sysexList.stream()
                    .sorted(Comparator.comparing(Sysex::getName))
                    .toList();
        } catch (IOException ioe) {
            logger.error("Exception: CAUSE: {}", ioe.getMessage());
        }
    }

    public Sysex getSysexByName(String name) {
        return sysexList.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }
}
