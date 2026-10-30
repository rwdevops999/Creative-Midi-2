package scenes.base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.routing.Router;
import util.app.scene.IScene;

import static util.app.colors.ColorScheme.getColor;
import static util.util.setPaneBackground;

public class BaseClosePane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(BaseClosePane.class);

    private static final int PANE_HEIGHT = 30;

    public BaseClosePane() {
        super();

        setId("ClosePane");

        setPrefHeight(PANE_HEIGHT);
        setAlignment(Pos.CENTER);
    }

    public BaseClosePane(IScene callingScene, EventHandler<ActionEvent> handler) {
        this();

/*        if (handler != null) {
            closeHandler = handler;
        }
*/
        buildPane();

        Background background = new Background(new BackgroundFill(getColor(), null, null));
        setBackground(background);
        setPrefHeight(PANE_HEIGHT);
        setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        closeButton.setId("CloseButton");

        closeButton.setOnAction(e -> {
/*            try {
                SysData.getInstance().save();
            } catch (Exception ex) {
                logger.error("[CM_CLOSE_PANE] Exception. CAUSE {}", ex.getMessage());
            }

            if (closeHandler != null) {
                closeHandler.handle(null);
            }

            CommunicationModel.clearStatus();
*/
            Router.getInstance().routeTo(null, callingScene, true);
        });

        getChildren().addAll(closeButton);

        logger.debug("[CM_CLOSE_PANE] Built {}", getId());
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        setPaneBackground(this);

        logger.debug("Built {}", getId());
    }

/*    public void setActionHandler(EventHandler<ActionEvent> handler) {
        closeHandler = handler;
    }
*/
}
