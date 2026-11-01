package scenes;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.SysexPane;
import util.Util;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SceneActionsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SceneActionsPane.class);

    public SceneActionsPane() {
        super();

        setId("SceneActionsPane");
    }

    private Pane parent;

    public SceneActionsPane(Pane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "blue"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    // ACCESSOR
    public SysexPane getSysexPane() {
        return (SysexPane) parent;
    }
}
