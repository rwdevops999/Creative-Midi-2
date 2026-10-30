package util.app.colors;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorScheme {
    private static final Map<ColorKey, Color> colors = new HashMap<>();

    public record ColorKey(String arg1, String arg2, String arg3) {
        public ColorKey(String arg1) {
            this(arg1, null, null);
        }
        public ColorKey(String arg1, String arg2) {
            this(arg1, arg2, null);
        }
    }

    // Define the colors here
    static {
        // root color
        colors.put(new ColorKey(null, null, null), Color.DARKGRAY);

        // borders
        // borders
        colors.put(new ColorKey("border", "red", null), Color.RED);
        colors.put(new ColorKey("border", "green", null), Color.GREEN);
        colors.put(new ColorKey("border", "blue", null), Color.BLUE);
        colors.put(new ColorKey("border", "orange", null), Color.ORANGE);
    }

    public static Color getColor(ColorKey key) {
        if (colors.containsKey(key)) {
            return colors.get(key);
        }

        ColorKey nextKey = new ColorKey(key.arg1, key.arg2, null);
        if (colors.containsKey(nextKey)) {
            return colors.get(nextKey);
        }

        nextKey = new ColorKey(key.arg1, null, null);
        if (colors.containsKey(nextKey)) {
            return colors.get(nextKey);
        }

        return colors.get(new ColorKey(null, null, null));
    }

    public static Color getColor(String... args) {
        ColorKey key = new ColorKey(null, null, null);
        if (args != null && args.length > 0) {
            if (args.length == 1) {
                key = new ColorKey(args[0], null, null);
            } else if (args.length == 2) {
                key = new ColorKey(args[0], args[1], null);
            } else if (args.length == 3) {
                key = new ColorKey(args[0], args[1], args[2]);
            }
        }

        return getColor(key);
    }

    public static void addColorsToScheme(Map<ColorKey, Color> newColors) {
        if (newColors != null) {
            colors.putAll(newColors);
        }
    }
}
