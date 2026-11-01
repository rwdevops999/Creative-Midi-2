package app.root.root;

import fx.dialog.AboutDialog;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.CreativeApp;
import app.root.root.footer.FooterPane;
import app.root.root.header.HeaderPane;
import scenes.main.MainScene;
import util.app.AppInfo;
import util.app.routing.Router;
import util.properties.AppProperties;

import java.util.Objects;

import static util.app.colors.ColorScheme.getColor;
import static util.properties.PropertyKeys.*;
import static util.Util.*;

public class MainPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(MainPane.class);

    public MainPane() {
        super();

        setId("MainPane (SPAOwner)");
    }

    private RootPane parent;

    public MainPane(RootPane parent, int width, int height) {
        this();

        this.parent = parent;

        setPaneSizeAsPercentage(this, parent, width, height);
        setPaneBackground(this);

//        showPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private void buildPane() {
        logger.info("Building {}", getId());

        if (AppProperties.getInstance().getPropertyAsBoolean(APP_RENDER_BACKGROUND, true)) {
            // 1. Set a background
            Image image = new Image(Objects.requireNonNull(CreativeApp.class.getClassLoader().getResourceAsStream("backgrounds/background.png")));
            BackgroundFill backgroundColor = new BackgroundFill(getColor(), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY);
            BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO,
                            BackgroundSize.AUTO,
                            false,
                            false,
                            true,
                            false
                    )
            );
            setBackground(new Background(new BackgroundFill[]{backgroundColor}, new BackgroundImage[]{backgroundImage}));
        }

        if (AppProperties.getInstance().getPropertyAsBoolean(APP_RENDER_ABOUT, true)) {
            AboutDialog aboutDialog = new AboutDialog(AppProperties.getInstance().getPropertyAsString(APP_VERSION, "v0.0.0"));

            int seconds = AppProperties.getInstance().getPropertyAsInteger(APP_RENDER_ABOUT_TIMING, 2);
            PauseTransition delay = new PauseTransition(Duration.seconds(seconds));

            // 3. Define the action when the timer finishes
            delay.setOnFinished(event -> {
                aboutDialog.getDialogPane().getScene().getWindow().hide();
                continueApplication();
            });

            // 4. Start the timer
            delay.play();

            // 5. Display the dialog and wait
            aboutDialog.showAndWait();

        }

        logger.info("Built {}", getId());
    }

    private void continueApplication() {
        // Set Header
        setTop(new HeaderPane(this, 100, 15));

        // Set Footer
        setBottom(new FooterPane(this, 100, 10));

        // This pane will contain all SPA's
        AppInfo.INSTANCE.setSpaOwner(this);

        Router.getInstance().routeTo(null, new MainScene(), true);
    }

    // ACCESSORS
    RootPane getRootPane() {
        return parent;
    }
}
