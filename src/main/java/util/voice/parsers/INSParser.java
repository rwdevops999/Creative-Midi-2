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

    @Getter
    private static INSParser instance = new INSParser();

    private InsProvider provider;
    private File file;

    private INSParser() {
        provider = null;
        file = null;
    }

    private String lastProcessedFile = null;

    public void startParsing(InsProvider provider, File file) {
        this.provider = provider; // we can use the provider to find the linkedpatch in the MS2S file
        this.file = file;

        if (lastProcessedFile == null || !lastProcessedFile.equals(file.getName())) {
            System.out.println("PARSING: " + file.getName());
            parse(this.file);
            lastProcessedFile = file.getName();
        }
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
    private int unmatchedPatches = 0;
    private int groupNr;

    public void parse(File file) {
        numPatches = 0;
        unmatchedPatches = 0;
        rootGroups = new ArrayList<>();
        groupNr = 1;
        Map<String, String[]> banks = new HashMap<>();

        // TODO add INS parsing
        try (Scanner scanner =  new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                parseLine(line, banks);
            }
        } catch (IOException ioe) {
            logger.error("[CM_MS2S_PARSER] Exception. CAUSE: {}", ioe.getMessage());
        }

        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("Patches found in " + file.getName() + " = " + numPatches);
            System.out.println("Unmatched patches = " + unmatchedPatches);
        }
    }

    private String currentBank;
    private void parseLine(String line, Map<String, String[]> banks) {
        if (! line.startsWith(";") &&! line.startsWith("Key") && ! line.startsWith("Drum")) {
            if (line.startsWith("[")) {
                String bankName = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                currentBank = bankName;
                banks.computeIfAbsent(bankName, k -> new String[128]);
            } else if (line.startsWith("BasedOn")) {
                int separator = line.indexOf("=");
                String value = line.substring(separator + 1).trim();
                System.arraycopy(banks.get(value), 0, banks.get(currentBank), 0, banks.get(value).length);
            } else if (line.startsWith("Control")) {
                Group group = new Group(groupNr++, currentBank, null);
                rootGroups.add(group);
                currentGroup = group;
            } else if (line.startsWith("Patch")) {
                int separator = line.indexOf("=");
                String descr = line.substring(0, separator).trim();
                int index = descr.indexOf("[");
                int banknr = Integer.parseInt(descr.substring(index + 1, descr.indexOf("]")).trim());
                String groupName = line.substring(separator + 1).trim();
                Group group = new Group(groupNr, groupName, currentGroup);
                CreatePatches(group, banks, groupName, banknr);
            } else if (line.contains("=")) {
                int separator = line.indexOf("=");
                int index = Integer.parseInt(line.substring(0, separator).trim());
                String value = line.substring(separator + 1).trim();
                banks.get(currentBank)[index] = value;
            }
        }
    }

    private void CreatePatches(Group group, Map<String, String[]> banks, String bankName, int banknr) {
        if (banks.get(bankName) != null) {
            for (int pc = 0; pc < banks.get(bankName).length; pc++) {
                if (banks.get(bankName)[pc] != null) {
                    int msb = (banknr >> 7);
                    int lsb = (banknr & 0x7F);

                    String name = banks.get(bankName)[pc];
                    Patch linkedPatch = provider.findPatch(msb, lsb, pc);
                    if (linkedPatch != null) {
                        Patch patch = new Patch(group, linkedPatch.getType(), name, ""+msb, ""+lsb, ""+pc);
                        patch.setLinkedPatch(linkedPatch);

                        if (group != null) {
                            group.getPatches().add(patch);
                            numPatches++;
                        }
                    } else {
                        System.out.println("Unmatched patch: " + name + "(" + msb + "," + lsb + "," + pc + ")");
                        unmatchedPatches++;
                    }
                }
            }
        }
    }
}
