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

        parent = owner;

        buildPane();
    }

    private VoiceListPane voiceListPane;
    private VoiceFilterPane voiceFilterPane;
    private void buildPane() {
        logger.debug("building {}", getId());

        voiceListPane = new VoiceListPane(this, 79.9);
        voiceFilterPane = new  VoiceFilterPane(this, 9.8);

        getChildren().add(voiceFilterPane);
        getChildren().add(voiceListPane);
        getChildren().add(new  VoicePlayPane(this, 9.8));

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoicePane getVoicePane() {
        return parent;
    }

    public VoiceListPane getVoiceListPane() {
        return voiceListPane;
    }

    public VoiceFilterPane getVoiceFilterPane() {
        return voiceFilterPane;
    }
}

