package util;

import javafx.scene.Scene;
import lombok.Getter;

public class Theme {
    @Getter
    private static final Theme instance = new Theme();

    private Theme() {
    }

    private ThemeType appTheme = ThemeType.LIGHT;

    public boolean isModeLight() {
        return appTheme == ThemeType.LIGHT;
    }

    public String getThemeIcon() {
        if (isModeLight()) {
            return "icons/bulb-off.png";
        }

        return "icons/bulb-on.png";
    }

    public void switchMode(boolean mode) {
        appTheme = ThemeType.DARK;

        if (mode) {
            appTheme = ThemeType.LIGHT;
        }
    }

    public void switchTheme(Scene scene) {
        if (appTheme == ThemeType.LIGHT) {
            if (scene != null) {
                scene.getRoot().setStyle("");
            }
        } else {
            if (scene != null) {
                scene.getRoot().setStyle("-fx-base: #4d4d4d;");
            }
        }
    }

    public enum ThemeType {
        LIGHT,
        DARK
    }
}
