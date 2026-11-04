package util.voice.parsers;

import entity.voice.Group;
import entity.voice.Patch;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.voice.InsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class INSParser {
    private static final Logger logger = LoggerFactory.getLogger(INSParser.class);

    private InsProvider provider;
    private File file;

    public INSParser() {
        super();

        provider = null;
        file = null;
    }

    public INSParser(InsProvider provider, File file) {
        this();

        this.provider = provider; // we can use the provider to find the linkedpatch in the MS2S file
        this.file = file;
        parse(this.file);
    }

    // for INS these are the banks as INS doesn't know groups like MS2S.
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

        // TODO add INS parsing


        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("Patches found in " + file.getName() + " = " + numPatches);
        }
    }
}
