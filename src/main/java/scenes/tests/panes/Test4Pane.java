package scenes.tests.panes;

import entity.sysex.Sysex;
import javafx.scene.layout.HBox;
import scenes.eventlist.filter.subpanes.ChannelControlPane;
import scenes.eventlist.filter.subpanes.ChannelFilterPane;
import scenes.eventlist.filter.subpanes.MidiTypeFilterPane;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.parametrized.Parameter;
import scenes.sysex.parametrized.ParametrizedFunctionFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static util.Util.calcNote;
import static util.Util.setPaneBackground;

public class Test4Pane extends HBox {
    public Test4Pane() {
        super();

        setId("Test4Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
    }

    private void runTest() {
        String test = "XG_PARAMETER_CHANGE";

        List<String> acronyms = Arrays.asList("XG", "CC", "SysEx");

        String pascal = toPascalCaseWithAcronyms(test, acronyms);
    }

    public static String toPascalCaseWithAcronyms(String input, List<String> acronyms) {
        if (input == null || input.isEmpty()) return "";

        String[] words = input.split("_");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            // Check if the current word matches any acronym in your list (case-insensitive match)
            String matchedAcronym = findAcronymMatch(word, acronyms);

            if (matchedAcronym != null) {
                // Use the exact casing from your array
                sb.append(matchedAcronym);
            } else {
                // Standard Pascal Case: Capital First, lowercase rest
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    sb.append(word.substring(1).toLowerCase());
                }
            }
            sb.append(" "); // Add space between words
        }

        return sb.toString().trim();
    }

    private static String findAcronymMatch(String word, List<String> acronyms) {
        if (acronyms == null) return null;

        for (String acronym : acronyms) {
            if (acronym.equalsIgnoreCase(word)) {
                return acronym; // Return the correctly cased acronym from your list
            }
        }
        return null;
    }
}
