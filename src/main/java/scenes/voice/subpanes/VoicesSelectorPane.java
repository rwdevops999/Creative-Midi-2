package scenes.voice.subpanes;

import fx.component.ChannelIndicator;
import fx.component.LightIndicatorButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import scenes.voice.VoiceFilterPane;
import util.Util;
import util.app.colors.ColorScheme;

import java.util.HashMap;
import java.util.Map;

import static util.Constants.*;
import static util.app.colors.ColorScheme.getColor;

public class VoicesSelectorPane extends HBox {
    public VoicesSelectorPane() {
        super();

        setId("VoicesSelectorPane");

        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
    }

    Map<Integer, ChannelIndicator> ledMap = new HashMap<>();
    Map<Integer, BooleanProperty> listen = new HashMap<>();

    private VoiceFilterPane parent;

    public VoicesSelectorPane(VoiceFilterPane owner) {
        this();

        this.parent = owner;

        ledMap.put(CHANNEL_RIGHT1, new ChannelIndicator("Ch 1:", getColor("voice", "ledRed", "on"), getColor("voice", "ledRed", "off")));
        ledMap.put(CHANNEL_LEFT, new ChannelIndicator("Ch 2 (⇧):", getColor("voice", "ledGreen", "on"), getColor("voice", "ledGreen", "off")));
        ledMap.put(CHANNEL_RIGHT2, new ChannelIndicator("Ch 3 (⌃):", getColor("voice", "ledBlue", "on"), getColor("voice", "ledBlue", "off")));

        listen.put(CHANNEL_RIGHT1, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_LEFT, new SimpleBooleanProperty(true));
        listen.put(CHANNEL_RIGHT2, new SimpleBooleanProperty(true));

        buildPane();
    }

    private void buildPane() {
        Label lblRight1 = new Label("Select a voice file");

        LightIndicatorButton right1 = new LightIndicatorButton("Right1", 43, "blue");
        right1.selectedProperty().bindBidirectional(listen.get(CHANNEL_RIGHT1));
/*        right1.setOnAction(event -> {
            listen.put(CHANNEL_RIGHT1, right1.isSelected());
        }); */
        getChildren().add(right1);
        getChildren().add(ledMap.get(CHANNEL_RIGHT1));

        LightIndicatorButton left = new LightIndicatorButton("Left", 43, "blue");
        left.selectedProperty().bindBidirectional(listen.get(CHANNEL_LEFT));
/*        left.setOnAction(event -> {
            listen.put(CHANNEL_LEFT, left.isSelected());
        }); */
        getChildren().add(left);
        getChildren().add(ledMap.get(CHANNEL_LEFT));

        LightIndicatorButton right2 = new LightIndicatorButton("Right2", 43, "blue");
        right2.selectedProperty().bindBidirectional(listen.get(CHANNEL_RIGHT2));
/*        right2.setOnAction(event -> {
            listen.put(CHANNEL_RIGHT2, right2.isSelected());
        }); */
        getChildren().add(right2);
        getChildren().add(ledMap.get(CHANNEL_RIGHT2));
    }

    // ACCESSORS
    public VoiceFilterPane getVoiceFilterPane() {
        return parent;
    }
}
