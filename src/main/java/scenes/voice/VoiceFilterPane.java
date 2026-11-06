package scenes.voice;

import fx.component.LightIndicatorButton;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class VoiceFilterPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceFilterPane.class);

    public VoiceFilterPane() {
        super();

        setId("VoiceFilterPane");

        setSpacing(10);
    }

    private VoiceResultPane parent;
    public VoiceFilterPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "red"));

        setPaneBackground(this);

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        LightIndicatorButton right1 = new LightIndicatorButton("Right1", 40, "blue");
        getChildren().add(right1);

        LightIndicatorButton left = new LightIndicatorButton("Left", 40, "blue");
        getChildren().add(left);

        LightIndicatorButton right2 = new LightIndicatorButton("Left", 40, "blue");
        getChildren().add(right2);

        LightIndicatorButton favorites = new LightIndicatorButton("★", 40, "orange");
        getChildren().add(favorites);

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
