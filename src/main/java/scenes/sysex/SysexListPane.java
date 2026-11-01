package scenes.sysex;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SysexListPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SysexListPane.class);

    public SysexListPane() {
        super();

        setId("SysexListPane");
    }

    private SysexPane parent;
    public SysexListPane(SysexPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
