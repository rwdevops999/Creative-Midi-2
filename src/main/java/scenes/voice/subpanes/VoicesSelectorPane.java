package scenes.voice.subpanes;

import fx.component.ChannelIndicator;
import fx.component.LightIndicatorButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import lombok.Getter;
import scenes.voice.VoiceFilterPane;
import util.Util;
import util.app.colors.ColorScheme;
import util.midi.MidiHelper;

import java.util.HashMap;
import java.util.Map;

import static util.Constants.*;
import static util.app.colors.ColorScheme.getColor;

public class VoicesSelectorPane extends HBox {
    private MidiHelper midiHelper = MidiHelper.getInstance();

    public VoicesSelectorPane() {
        super();

        setId("VoicesSelectorPane");

        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
    }

    private VoiceFilterPane parent;

    public VoicesSelectorPane(VoiceFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private void buildPane() {
        LightIndicatorButton right1 = new LightIndicatorButton("Right1", 43, "blue", null);
        right1.selectedProperty().bindBidirectional(midiHelper.getListen().get(CHANNEL_RIGHT1));
        getChildren().add(right1);
        getChildren().add(midiHelper.getLedMap().get(CHANNEL_RIGHT1));

        LightIndicatorButton left = new LightIndicatorButton("Left", 43, "blue", null);
        left.selectedProperty().bindBidirectional(midiHelper.getListen().get(CHANNEL_LEFT));
        getChildren().add(left);
        getChildren().add(midiHelper.getLedMap().get(CHANNEL_LEFT));

        LightIndicatorButton right2 = new LightIndicatorButton("Right2", 43, "blue", null);
        right2.selectedProperty().bindBidirectional(midiHelper.getListen().get(CHANNEL_RIGHT2));
        getChildren().add(right2);
        getChildren().add(midiHelper.getLedMap().get(CHANNEL_RIGHT2));

        Separator separator = new Separator(Orientation.VERTICAL);
        getChildren().add(separator);

        LightIndicatorButton style = new LightIndicatorButton("Style", 43, "blue", null);
        style.selectedProperty().bindBidirectional(midiHelper.getListen().get(CHANNEL_STYLE));
        getChildren().add(style);
        getChildren().add(midiHelper.getLedMap().get(CHANNEL_STYLE));
    }

    // ACCESSORS
    public VoiceFilterPane getVoiceFilterPane() {
        return parent;
    }
}
