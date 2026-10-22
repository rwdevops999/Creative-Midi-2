package util.app;

import lombok.Getter;
import lombok.Setter;

public enum AppInfo {
        INSTANCE; // The single instance

        @Getter
        @Setter
        private boolean debugMode = false;

        // Add your properties and methods here
}
