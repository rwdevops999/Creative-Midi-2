package scenes.voice;

import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceFilterPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceFilterPane.class);

    public VoiceFilterPane() {
        super();

        setId("VoiceFilterPane");
    }

    private VoiceResultPane parent;
    public VoiceFilterPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
