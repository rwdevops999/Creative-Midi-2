package scenes.voice;

import fx.component.LightIndicatorButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.voice.subpanes.VoicesSelectorPane;
import util.Util;

import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class VoiceFilterPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceFilterPane.class);

    public VoiceFilterPane() {
        super();

        setId("VoiceFilterPane");

        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        setPadding(new Insets(0, 5, 0, 5));
    }

    private VoiceResultPane parent;
    public VoiceFilterPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
//        Util.showSmallPaneBorder(this, getColor("border", "red"));

        setPaneBackground(this);

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 2. Zorg ervoor dat de spacer alle resterende horizontale ruimte inneemt
        LightIndicatorButton favorites = new LightIndicatorButton("★", 43, "orange");
        favorites.getButtonLabel().setTextFill(Color.GOLD);

        VoicesSelectorPane voicesSelectorPane = new VoicesSelectorPane(this);

        getChildren().addAll(favorites, spacer, voicesSelectorPane);

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
