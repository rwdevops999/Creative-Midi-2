package scenes.sysex;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SysexDetailsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SysexDetailsPane.class);

    public SysexDetailsPane() {
        super();

        setId("SysexDetailsPane");

        setPadding(new Insets(5));
        setSpacing(2);
    }

    private SysexPane parent;
    public SysexDetailsPane(SysexPane owner, int width, int height) {
        this();

        parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "green"));

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
