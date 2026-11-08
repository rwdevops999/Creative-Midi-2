package scenes.voice;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.voice.subpanes.ExcludePane;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchExclusionPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchExclusionPane.class);

    public VoiceSearchExclusionPane() {
        super();

        setId("VoiceSearchExclusionPane");
    }

    private VoiceSearchPane parent;
    public VoiceSearchExclusionPane(VoiceSearchPane owner, double height) {
        this();

        Util.setPaneHeightAsPercentage(this, owner, height);

        this.parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        getChildren().clear();
        ExcludePane excludePane = new ExcludePane(this);
        getChildren().add(excludePane);

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return parent;
    }
}
