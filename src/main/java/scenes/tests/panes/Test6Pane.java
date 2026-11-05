package scenes.tests.panes;

import entity.voice.Group;
import entity.voice.Patch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import util.app.AppInfo;
import util.registry.Registry;
import util.voice.InstrumentProvider;
import util.voice.InstrumentProviderFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static util.Util.setPaneBackground;

public class Test6Pane extends VBox {
    public Test6Pane() {
        super();

        setId("Test6Pane");

        setPaneBackground(this);

        Registry.getInstance().showStatus("Running Test6");

        setupTest();
        runTest();
    }

    private int patchesToConvert = 0;
    private Map<Integer, String[]> banks;
    private void setupTest() {
        InstrumentProvider provider = InstrumentProviderFactory.getProvider("PSR-SX600.txt");

        if (provider != null) {
            banks = new HashMap<>();

            List<Group> groups = provider.getGroups();

            for (Group group : groups) {
                processGroup(group);
            }
        }

        makePatches();

        writeToFile();

        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("PATCHES To Convert = " + patchesToConvert);
        }
    }

    private void processGroup(Group group) {
        for (Patch patch : group.getPatches()) {
            patchesToConvert++;
            int bankNr = (patch.getMsb() << 7) | patch.getLsb();
            banks.computeIfAbsent(bankNr, k -> new String[128]);
            banks.get(bankNr)[patch.getPc()] = patch.getName();
        }

        if (! group.getGroups().isEmpty()) {
            for (Group group2 : group.getGroups()) {
                processGroup(group2);
            }
        }
    }

    Map<Integer, String> patches;
    private void makePatches() {
        patches = new HashMap<>();

        List<Integer> set = banks.keySet().stream().sorted().toList();

        for (Integer i : set) {
            patches.put(i, "Bank " + i);
        }
    }

    private void writeToFile() {
        System.out.println("; *******************************************************************************************");
        System.out.println(".Patch Names");
        System.out.println("; *******************************************************************************************");

        List<Integer> set = banks.keySet().stream().sorted().toList();
        for (Integer i : set) {
            System.out.println("[Bank " + i + "]");

            for (int j = 0; j < banks.get(i).length; j++) {
                if (banks.get(i)[j] != null) {
                    System.out.println(j + "=" + banks.get(i)[j]);
                }
            }

            System.out.println(" ");
        }

        System.out.println("; *******************************************************************************************");
        System.out.println(".Instrument Definitions");
        System.out.println("; *******************************************************************************************");

        for (Integer k : patches.keySet().stream().sorted().toList()) {
            System.out.println("Patch["+k+"]="+patches.get(k));
        }
    }

    private void runTest() {
    }
}
