package scenes;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;

public class CreativeApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CreativeApp.class);

    private static AppInfo appInfo = AppInfo.INSTANCE;

    private static final String TEST = "-t";

    public static void startup(String[] args) {
        for (String arg : args) {
            if (TEST.equalsIgnoreCase(arg)) {
                appInfo.setTestMode(true);
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting Creative Midi");
    }
}
