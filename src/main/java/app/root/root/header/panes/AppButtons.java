package app.root.root.header.panes;

import fx.component.IconButton;
import fx.dialog.DialogFactory;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.fusesource.jansi.AnsiConsole;
import util.Theme;
import util.Util;
import util.app.AppInfo;
import util.midi.MidiHelper;
import util.midi.MidiWriter;
import util.registry.Registry;

import java.util.Objects;

public class AppButtons extends GridPane {
    public AppButtons() {
        super();

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
        IconButton exitButton = new IconButton("exit", "Exit creative midi", "icons/exit.png", e -> {
            // 1. Save User data
            // TODO Add User Data Save

            if (MidiHelper.getInstance().isMidiPlaying()) {
                MidiHelper.getInstance().stopMidiFile( null);
            }

            // 3. Exit
            AnsiConsole.systemUninstall();
            System.exit(0);
        });
        add(exitButton, 1, 0);
        IconButton midiResetButton = new IconButton("midiReset", "Shut-up midi", "icons/midi.png", e -> {
            if (AppInfo.INSTANCE.getSelectedDevice() != null) {
                Registry.getInstance().showStatus("Resetting midi");
                MidiWriter.getInstance().reset();
            } else {
                DialogFactory.getInstance().renderErrorDialog("No device selected for MIDI");
            }
        });
        add(midiResetButton, 1, 1);
        IconButton midiSoundButton = new IconButton("midiSound", "Play midi sound", "icons/tuning-fork.png", e -> {
            if (AppInfo.INSTANCE.getSelectedDevice() != null) {
                Registry.getInstance().showStatus("Testing midi");
                MidiWriter.getInstance().testTone();
            } else {
                DialogFactory.getInstance().renderErrorDialog("No device selected for MIDI");
            }
        });
        add(midiSoundButton, 0, 1);

        Theme themeInstance = Theme.getInstance();

        ToggleButton themeButton = new ToggleButton();
        themeButton.setSelected(!themeInstance.isModeLight());
        setButtonIcon(themeButton, themeInstance.getThemeIcon());
        Tooltip tooltipField = new Tooltip("Change theme");
        themeButton.setTooltip(tooltipField);
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
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        button.setGraphic(imageView);
    }
}
