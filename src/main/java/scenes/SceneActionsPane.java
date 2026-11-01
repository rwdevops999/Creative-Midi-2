package scenes;

import entity.NameEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.SysexPane;
import util.Util;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class SceneActionsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SceneActionsPane.class);

    public SceneActionsPane() {
        super();

        setId("SceneActionsPane");

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(5, 0, 0, 0));
        setSpacing(5);
    }

    private Pane parent;

    public SceneActionsPane(Pane owner, int width, int height, BiConsumer<NameEntity, Pane>[] consumers, Supplier<NameEntity> supplier) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "blue"));

        setPaneBackground(this);

        buildPane(consumers);
    }

    private static final int BUTTON_WIDTH = 100;
    private void buildPane(BiConsumer<NameEntity, Pane>[] consumers) {
        logger.debug("Building {}", getId());

        for (BiConsumer<NameEntity, Pane> consumer : consumers) {
            if (consumer instanceof NameEntity nameConsumer) {
                Button button = new Button(nameConsumer.getName());
                button.setMaxWidth(BUTTON_WIDTH);
                button.setPrefWidth(BUTTON_WIDTH);
                button.setMinWidth(BUTTON_WIDTH);
                button.setId(nameConsumer.getName());
                button.setVisible(false);
                getChildren().add(button);
            }
        }

        logger.debug("Built {}", getId());
    }

    public void hideAllButtons() {
        getChildren().forEach(node -> node.setVisible(false));
    }

    public void showButton(String name, boolean show) {
        Button button = (Button)lookup("#" + name);
        if (button != null) {
          button.setVisible(show);
        }
    }

    // ACCESSOR
    public SysexPane getSysexPane() {
        return (SysexPane) parent;
    }
}
