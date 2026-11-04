package util.voice.parsers;

import entity.voice.Group;
import entity.voice.Patch;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class INSParser {
    private static final Logger logger = LoggerFactory.getLogger(INSParser.class);

    private File file;

    public INSParser() {
        super();

        file = null;
    }

    public INSParser(File file) {
        this();

        this.file = file;
        parse(this.file);
    }

    @Getter
    private List<Group> rootGroups = new ArrayList<>();

    @Getter
    private Set<String> types = new HashSet<>();

    @Getter
    private List<Patch> patches = new ArrayList<>();

    @Getter
    private Group currentGroup = null;

    private int numPatches = 0;

    public void parse(File file) {
        numPatches = 0;
        rootGroups = new ArrayList<>();

        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("Patches found in " + file.getName() + " = " + numPatches);
        }
    }
}
