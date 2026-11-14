package scenes.voice;

import entity.NameEntity;
import entity.midi.MidiEventInfo;
import entity.voice.Patch;
import fx.component.ChannelIndicator;
import fx.component.ComboValuePane;
import fx.component.LightIndicatorButton;
import fx.dialog.DialogFactory;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.base.Base;
import scenes.base.BaseClosePane;
import util.Util;
import util.app.AppInfo;
import util.midi.EventKeyType;
import util.midi.MidiHelper;
import util.voice.receiver.MultiChannelMidiReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class VoicePlayPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoicePlayPane.class);

    private MidiHelper midiHelper = MidiHelper.getInstance();

    public VoicePlayPane() {
        super();

        setId("VoicePlayPane");

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);

        setPadding(new Insets(5));
    }

    private VoiceResultPane parent;
    public VoicePlayPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);

        setPaneBackground(this);

        if (AppInfo.INSTANCE.getMidiToPlay() != null) {
            dsFilename.set(FilenameUtils.removeExtension(AppInfo.INSTANCE.getMidiToPlay().getName()));
        }
        buildPane();
    }

    private StringProperty dsFilename = new SimpleStringProperty("<No file loaded>");
    private File selectedFile = null;

    private BooleanProperty dsEnabled = new SimpleBooleanProperty(true);
    private ObjectProperty<NameEntity> dsValue = new SimpleObjectProperty<>();

    private LightIndicatorButton playButton;
    private void buildPane() {
        logger.debug("building {}", getId());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a MIDI file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MIDI", "*.mid")
        );

        playButton = new LightIndicatorButton("Play", 40, "red", null);
        setupPlayHandler(playButton);

        ComboValuePane markerSelectValuePane = new ComboValuePane(this, "🚩", dsValue, dsEnabled, new ArrayList<>(MidiHelper.getInstance().extractMarkers()), null, 20, 80, 2);

        dsValue.addListener((observable, oldValue, newValue) -> {
            if (midiHelper.isMidiPlaying()) {
                if (playButton != null) {
                    playButton.fire();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    playButton.fire();
                }
            }
        });

        Button load = new Button("Load...");
        load.setOnAction(e -> {
            if (AppInfo.INSTANCE.getWorkingDir() != null) {
                fileChooser.setInitialDirectory(new File(AppInfo.INSTANCE.getWorkingDir()));
            }
            selectedFile = fileChooser.showOpenDialog(AppInfo.INSTANCE.getPrimaryStage());

            if (selectedFile != null) {
                AppInfo.INSTANCE.setMidiToPlay(selectedFile);

                List<MidiEventInfo> midiEventInfos = MidiHelper.getInstance().extractMarkers();
                List<MidiEventInfo> markers = midiEventInfos.stream().filter(mei -> mei.getEventEventKey().equals(EventKeyType.META_MARKER)).toList();

                markerSelectValuePane.updateComboValues(new ArrayList<>(markers));

                dsFilename.set(FilenameUtils.removeExtension(selectedFile.getName()));
            }
        });

        Label fileName = new Label();
        fileName.textProperty().bindBidirectional(dsFilename);

        fileName.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(playButton, load, fileName, spacer, markerSelectValuePane);

        logger.debug("built {}", getId());
    }

    private void setupPlayHandler(LightIndicatorButton play) {
        Map<Integer, ChannelIndicator> ledMap = midiHelper.getLedMap();

        try (MultiChannelMidiReceiver multiChannelMidiReceiver = new MultiChannelMidiReceiver(ledMap)) {
            play.setOnAction((e) -> {
                MidiEventInfo marker = (MidiEventInfo) dsValue.get();
                final Long startTick;
                if (marker != null) {
                    startTick = marker.getTick();
                } else {
                    startTick = null;
                }

                if (play.isSelected()) {
                    if (AppInfo.INSTANCE.getMidiToPlay() != null) {
                        Map<Integer, Patch> patches = getVoiceResultPane().getVoiceListPane().getChannelPatches();
                        BaseClosePane closePane = ((Base)AppInfo.INSTANCE.getSpa()).getClosePane();
                        closePane.setActionHandler(event -> {
                            if (midiHelper.isMidiPlaying()) {
                                midiHelper.stopMidiFile(null);
                            }
                        });

                        for (Integer channel : patches.keySet()) {
                            Patch selectedPatch = patches.get(channel);
                            midiHelper.sendAsMidi(channel, selectedPatch);
                        }

                        if (!midiHelper.playMidiFile(multiChannelMidiReceiver, play, startTick)) {
                            play.setSelected(false);
                        }
                    } else {
                        DialogFactory.getInstance().renderErrorDialog("No midi file selected to play");
                        play.setSelected(false);
                    }
                } else {
                    BaseClosePane closePane = ((Base)AppInfo.INSTANCE.getSpa()).getClosePane();
                    closePane.setActionHandler(null);
                    midiHelper.stopMidiFile(ledMap);
                }
            });
        }
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
