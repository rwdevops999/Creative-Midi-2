package util.app;

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

        // Add your properties and methods here
}
