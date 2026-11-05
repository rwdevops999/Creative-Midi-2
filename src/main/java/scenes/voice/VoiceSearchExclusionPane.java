package scenes.voice;

import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchExclusionPane extends TabPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchExclusionPane.class);

    public VoiceSearchExclusionPane() {
        super();

        setId("VoiceSearchExclusionPane");
    }

    private VoiceSearchPane parent;
    public VoiceSearchExclusionPane(VoiceSearchPane owner, double height) {
        this();

        Util.setControlHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "green"));

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
