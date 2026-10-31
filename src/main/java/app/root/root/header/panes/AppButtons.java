package app.root.root.header.panes;

import fx.component.IconButton;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.fusesource.jansi.AnsiConsole;
import util.Theme;
import util.Util;
import util.app.AppInfo;

import java.util.Objects;

public class AppButtons extends GridPane {
    public AppButtons() {
        super();
    }

    public AppButtons(Pane owner) {
        this();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHalignment(HPos.CENTER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHalignment(HPos.CENTER);

        getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        row1.setValignment(VPos.CENTER);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        row2.setValignment(VPos.CENTER);

        getRowConstraints().addAll(row1, row2);

        buildPane();
    }

    private void buildPane() {
        IconButton exitButton = new IconButton("exit", "exit Creative Midi", "icons/exit.png", e -> {
            // 1. Save User data
            // TODO Add User Data Save
            // 2. Shut-up midi playing
            // TODO Add Midi Player stop
            // 3. Exit
            AnsiConsole.systemUninstall();
            System.exit(0);
        });
        add(exitButton, 1, 0);
        IconButton midiResetButton = new IconButton("midiReset", "shutup midi", "icons/midi.png", e -> {
            // TODO Provide MidiWriter which also contains this
        });
        add(midiResetButton, 1, 1);
        IconButton midiSoundButton = new IconButton("midiSound", "sound midi", "icons/tuning-fork.png", e -> {
            // TODO Add To MidiWriter
        });
        add(midiSoundButton, 0, 1);

        Theme themeInstance = Theme.getInstance();

        ToggleButton themeButton = new ToggleButton();
        themeButton.setSelected(!themeInstance.isModeLight());
        setButtonIcon(themeButton, themeInstance.getThemeIcon());
        themeButton.setOnAction(e -> {
            Theme.getInstance().switchMode(!themeButton.isSelected());
            setButtonIcon(themeButton, themeInstance.getThemeIcon());
            themeInstance.switchTheme(AppInfo.INSTANCE.getRootScene());
        });
        add(themeButton, 0, 0);
    }

    public static void setButtonIcon (ButtonBase button, String iconname) {
        Image icon = new Image(Objects.requireNonNull(Util.class.getClassLoader().getResourceAsStream(iconname)));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);

        button.setGraphic(imageView);
    }
}
