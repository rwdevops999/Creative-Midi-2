package util.app;

import entity.device.DeviceInfo;
import entity.playlist.Song;
import entity.sysex.Sysex;
import entity.voice.KeyboardVoice;
import javafx.concurrent.Service;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import util.voice.InstrumentProvider;

import java.io.File;

public enum AppInfo {
        INSTANCE; // The single instance

        @Getter
        @Setter
        private boolean debugMode;

        @Getter
        @Setter
        private boolean testMode;

        @Getter
        @Setter
        private Stage primaryStage;

        // This one owns the spa (== MainPane)
        @Getter
        @Setter
        private Pane spaOwner;

        // This is an SPA (which contains functionality)
        @Getter
        @Setter
        private Pane spa;

        // This is the root scene
        @Getter
        @Setter
        private Scene rootScene;

        @Getter
        @Setter
        private DeviceInfo selectedDevice;

        // Add your properties and methods here
        @Setter
        private KeyboardVoice keyboardVoice;

        @Getter
        @Setter
        private Sysex sysex;

        @Getter
        @Setter
        private InstrumentProvider instrumentProvider;

        @Getter
        @Setter
        private boolean midifileChanged = false;

        @Getter
        @Setter
        private String lastVisitedFolder; // last visited by any FileChooser

        @Getter
        @Setter
        private File midiToPlay;

        @Getter
        @Setter
        private Song currentSong;

        @Getter
        @Setter
        private Service<Void> keyboardService;
}
