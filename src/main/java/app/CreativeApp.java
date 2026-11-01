package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.root.root.RootPane;
import util.Theme;
import util.app.AppInfo;
import util.app.Initialize;
import util.registry.Registry;

import java.util.Objects;

public class CreativeApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CreativeApp.class);

    private static final AppInfo appInfo = AppInfo.INSTANCE;

    private double xOffset = 0;
    private double yOffset = 0;

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
        new Initialize();

        // 2. Create root app
        RootPane rootPane = new RootPane(); // builds the main pane and monitor pane
        Scene rootScene = new Scene(rootPane);

        String css = Objects.requireNonNull(CreativeApp.class.getResource("/style/style.css")).toExternalForm();
        rootScene.getStylesheets().add(css);
        Theme.getInstance().switchTheme(rootScene);
        AppInfo.INSTANCE.setRootScene(rootScene);

        primaryStage.setScene(rootScene);
        primaryStage.show();

        Registry.getInstance().showStatus("Creative Midi Ready");

        // To move the screen around
        handleMouseEvents(primaryStage, rootPane);
    }

    private void handleMouseEvents(Stage primaryStage, RootPane rootPane) {
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }
}
