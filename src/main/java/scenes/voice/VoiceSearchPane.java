package scenes.voice;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneBackground;
import static util.Util.setPaneBackgroundWithColor;
import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchPane.class);

    public VoiceSearchPane() {
        super();

        setId("VoiceSearchPane");
    }

    private VoicePane parent;
    public VoiceSearchPane(VoicePane owner, int width) {
        this();

        Util.setPaneWidthAsPercentage(this, owner, width);
        Util.showSmallPaneBorder(this, getColor("border", "green"));

        setPaneBackground(this);

        parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        getChildren().add(new VoiceSearchSelectionPane(this, 59.8));
        getChildren().add(new VoiceSearchExclusionPane(this, 39.8));

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoicePane getVoicePane() {
        return parent;
    }
}

