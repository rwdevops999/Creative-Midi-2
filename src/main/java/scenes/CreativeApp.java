package scenes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.root.panes.RootPane;
import util.app.AppInfo;
import util.app.Initialize;

import java.util.Objects;

public class CreativeApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CreativeApp.class);

    private static final AppInfo appInfo = AppInfo.INSTANCE;

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
    public void start(Stage primaryStage) {
        logger.info("Starting Creative Midi");

        appInfo.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Creative Midi");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // 1. Initialize the app
        Initialize init = new Initialize();

        // 2. Create root app
        RootPane rootPane = new RootPane(); // builds the main pane and monitor pane
        Scene rootScene = new Scene(rootPane);
        String css = Objects.requireNonNull(CreativeApp.class.getResource("/style/style.css")).toExternalForm();
        rootScene.getStylesheets().add(css);


    }
}
