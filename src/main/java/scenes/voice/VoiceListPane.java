package scenes.voice;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceListPane extends StackPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceListPane.class);

    public VoiceListPane() {
        super();

        setId("VoiceListPane");
    }

    private VoiceResultPane parent;
    public VoiceListPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "green"));

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
