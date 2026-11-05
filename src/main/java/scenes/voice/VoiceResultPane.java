package scenes.voice;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.app.colors.ColorScheme.getColor;

public class VoiceResultPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceResultPane.class);

    public VoiceResultPane() {
        super();

        setId("VoiceResultPane");
    }

    private VoicePane parent;
    public VoiceResultPane(VoicePane owner, double width) {
        this();

        Util.setPaneWidthAsPercentage(this, owner, width);
        Util.showSmallPaneBorder(this, getColor("border", "blue"));

        parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        getChildren().add(new  VoiceFilterPane(this, 9.8));
        getChildren().add(new  VoiceListPane(this, 79.9));
        getChildren().add(new  VoicePlayPane(this, 9.8));

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoicePane getVoicePane() {
        return parent;
    }
}

