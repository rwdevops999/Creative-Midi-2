package util.app;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

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

        // This is a SPA (which contains functionality)
        @Getter
        @Setter
        private Pane spa;

        // Add your properties and methods here
}
