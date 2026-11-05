package util;

import entity.NoteEntity;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import util.app.colors.ColorScheme;
import util.properties.AppProperties;

import java.util.List;

import static util.Constants.BASE_NOTES;
import static util.properties.PropertyKeys.BASE_OCTAVE;

public class Util {
    public static void setPaneHeightAsPercentage(Pane child, Pane parent, int size) {
        double height = size / 100.0;

        child.minHeightProperty().bind(parent.heightProperty().multiply(height));
        child.maxHeightProperty().bind(parent.heightProperty().multiply(height));
        child.prefHeightProperty().bind(parent.heightProperty().multiply(height));
    }

    public static void setPaneWidthAsPercentage(Pane child, Pane parent, int size) {
        double width = size / 100.0;

        child.minWidthProperty().bind(parent.widthProperty().multiply(width));
        child.maxWidthProperty().bind(parent.widthProperty().multiply(width));
        child.prefWidthProperty().bind(parent.widthProperty().multiply(width));
    }

    public static void setControlWidthAsPercentage(Control control, Pane parent, int size) {
        double width = size / 100.0;

        control.minWidthProperty().bind(parent.widthProperty().multiply(width));
        control.maxWidthProperty().bind(parent.widthProperty().multiply(width));
        control.prefWidthProperty().bind(parent.widthProperty().multiply(width));
    }

    public static void setPaneSizeAsPercentage(Pane child, Pane parent, int width, int height) {
        setPaneWidthAsPercentage(child, parent, width);
        setPaneHeightAsPercentage(child, parent, height);
    }

    public static void setPaneBackground(Pane pane) {
        Background background = new Background(new BackgroundFill(ColorScheme.getColor(), null, null));
        pane.setBackground(background);
    }

    public static void setPaneBackgroundWithColor(Pane pane, Color color) {
        Background background = new Background(new BackgroundFill(color, null, null));
        pane.setBackground(background);
    }

    // This is for development only
    private static final int BORDER_SIZE=3;
    public static void showPaneBorder (Pane pane, Color color) {
//            pane.setStyle("-fx-border-color: blue; -fx-border-width: 1;");
        pane.setBorder(new Border(new BorderStroke(
                color,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(BORDER_SIZE)
        )));
    }

    public static void showSmallPaneBorder (Pane pane, Color color) {
        String s = ColorScheme.toWebColor(color);
        pane.setStyle("-fx-border-color: " + s + "; -fx-border-width: 1;");
    }

    public static NoteEntity calcNote (int notevalue) {
        int baseOctave = AppProperties.getInstance().getPropertyAsInteger(BASE_OCTAVE, 0);

        int noteIndex = notevalue % 12;
        int octave = -2 + baseOctave + (notevalue / 12);
        return new NoteEntity(notevalue, String.format("%s%d", BASE_NOTES[noteIndex], octave));
    }

    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Split by underscores, spaces, or hyphens
        String[] words = input.split("[_\\s-]+");
        StringBuilder pascalCaseString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize first letter, lowercase the rest
                pascalCaseString.append(" ").append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }

        return pascalCaseString.toString().trim();
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
