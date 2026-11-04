package util.voice.parsers;

import entity.voice.Group;
import entity.voice.Patch;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.voice.InstrumentProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MS2SParser {
    private static final Logger logger = LoggerFactory.getLogger(MS2SParser.class);

    private InstrumentProvider provider;
    private File file;

    public MS2SParser() {
        super();

        this.provider = null;
        file = null;
    }

    public MS2SParser(InstrumentProvider prov, File file) {
        this();

        this.provider = prov;
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

    private void parseGroup (String groupLine) {
        int groupNr = Integer.parseInt(groupLine.substring(2, groupLine.indexOf("]")));
        String groupName = groupLine.substring(groupLine.indexOf("]") + 1);

        if (groupNr == 1) {
            Group group = new Group(groupNr, groupName, null);
            rootGroups.add(group);
            currentGroup = group;
        } else {
            Group parentGroup = currentGroup;
            while (groupNr - 1 != parentGroup.getIndex()) {
                parentGroup = parentGroup.getParent();
            }

            Group group = new Group(groupNr, groupName, parentGroup);
            currentGroup = group;

            parentGroup.getGroups().add(group);
        }
    }

    private void parsePatch (String patchLine) {
        // use currentGroup to store
        try {
            String patchName = patchLine.substring(patchLine.indexOf("]") + 1);

            if (! patchName.isEmpty()) {
                // e.g Live! ConcertGrand
                int index = patchName.indexOf("!");
                if (index == -1 && AppInfo.INSTANCE.isDebugMode()) {
                    System.out.println("Found a voice without type:" + patchName);
                } else {
                    String type = patchName.substring(0, index);
                    types.add(type);

                    patchName = patchName.replace(type + "! ", "");

                    String data = patchLine.substring(
                            patchLine.indexOf(",") + 1,
                            patchLine.indexOf(']')
                    );

                    String[] dataArray = data.split(",");
                    Patch patch = new Patch(
                            currentGroup,
                            type,
                            patchName,
                            dataArray[1].trim(),
                            dataArray[2].trim(),
                            dataArray[0].trim());

                    numPatches++;

                    // TO GET THE CATEGORIES
                    if (currentGroup != null) {
                        currentGroup.getPatches().add(patch);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception. CAUSE: {}", e.getMessage());
        }
    }

    private void parseVoice(String voice) {
        if (!voice.isEmpty()) {
            if (voice.startsWith("[g")) {
                parseGroup (voice);
            } else if (voice.startsWith("[p")) {
                parsePatch (voice);
            }
        }
    }

    public void parse(File file) {
        numPatches = 0;
        rootGroups = new ArrayList<>();

        try (Scanner scanner =  new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if ("[end]".equals(line)) {
                    break;
                }
                //process each line in some way
                parseVoice(line);
            }
        } catch (IOException ioe) {
            logger.error("[CM_MS2S_PARSER] Exception. CAUSE: {}", ioe.getMessage());
        }

        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("Patches found in " + file.getName() + " = " + numPatches);
        }
    }
}
