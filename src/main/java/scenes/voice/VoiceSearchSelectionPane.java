package scenes.voice;

import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchSelectionPane extends TabPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchSelectionPane.class);

    public VoiceSearchSelectionPane() {
        super();

        setId("VoiceSearchSelectionPane");
    }

    private VoiceSearchPane parent;
    public VoiceSearchSelectionPane(VoiceSearchPane owner, double height) {
        this();

        Util.setControlHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "red"));

        this.parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return parent;
    }
}
