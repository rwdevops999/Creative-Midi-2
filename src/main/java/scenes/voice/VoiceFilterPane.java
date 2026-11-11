package scenes.voice;

import entity.voice.Patch;
import fx.component.LightIndicatorButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.voice.subpanes.VoiceInfoPane;
import scenes.voice.subpanes.VoicesSelectorPane;
import util.Constants;
import util.Util;
import util.midi.MidiHelper;
import util.user.UserData;

import java.util.ArrayList;

import static util.Constants.*;
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

    private LightIndicatorButton favorites;

    public VoiceFilterPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);

        setPaneBackground(this);

        buildPane();
    }

    private VoicesSelectorPane voicesSelectorPane;
    private VoiceInfoPane voiceInfoPane;

    private void buildPane() {
        logger.debug("building {}", getId());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // 2. Zorg ervoor dat de spacer alle resterende horizontale ruimte inneemt
        favorites = new LightIndicatorButton("★", 43, "orange", Color.GOLD);
        favorites.setOnAction(e -> {
            if (favorites.isSelected()) {
                getVoiceResultPane().getVoiceListPane().showResults(new ArrayList<>(UserData.getInstance().getFavorites()), true);
            } else {
                getVoiceResultPane().getVoiceListPane().showResults(null, false);
            }
        });

        voiceInfoPane = new VoiceInfoPane(this, 20, 100);

        voicesSelectorPane = new VoicesSelectorPane(this);

        getChildren().addAll(favorites, spacer1, voiceInfoPane, spacer2, voicesSelectorPane);

        logger.debug("built {}", getId());
    }

    public boolean isFavoritesShown() {
        return favorites.isSelected();
    }

    public void setRight1(Patch patch) {
        if (voiceInfoPane != null) {
            voiceInfoPane.setRight1Voice(patch.getName());
            MidiHelper.getInstance().sendAsMidi(CHANNEL_RIGHT1, patch);
        }
    }

    public void setLeft(Patch patch) {
        if (voiceInfoPane != null) {
            voiceInfoPane.setLeftVoice(patch.getName());
            MidiHelper.getInstance().sendAsMidi(CHANNEL_LEFT, patch);
        }
    }

    public void setRight2(Patch patch) {
        if (voiceInfoPane != null) {
            voiceInfoPane.setRight2Voice(patch.getName());
            MidiHelper.getInstance().sendAsMidi(CHANNEL_RIGHT2, patch);
        }
    }

    public void setVoice(int channel, Patch patch) {
        switch (channel) {
            case CHANNEL_RIGHT1:
                setRight1(patch);
                break;
            case CHANNEL_RIGHT2:
                setRight2(patch);
                break;
            case CHANNEL_LEFT:
                setLeft(patch);
                break;
            default: break;
        }
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }

    public VoicesSelectorPane getVoicesSelectorPane() {
        return voicesSelectorPane;
    }

    public VoiceInfoPane getVoicesInfoPane() {
        return voiceInfoPane;
    }
}
