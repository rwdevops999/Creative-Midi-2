package scenes.playlist;

import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.component.ScaleSelector;
import fx.component.SignatureSelector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.playlist.container.PlaylistContainer;
import scenes.sysex.statemachine.States;
import util.app.AppInfo;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SongDetailsPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(SongDetailsPane.class);

    public SongDetailsPane() {
        super();

        setId("SongDetailsPane");

        setPadding(new Insets(5, 5, 10, 5));
        setVgap(5);

        int[] columnSizes = {20,5,16,5,16,38};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }
    }

    private PlaylistDetailsPane parent;
    public SongDetailsPane(PlaylistDetailsPane owner, double height) {
        this();

        setPaneHeightAsPercentage(this, owner, height);
        showSmallPaneBorder(this, getColor("border", "orange"));

        parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    private final ObjectProperty<Integer> songIdProperty = new SimpleObjectProperty<>();

    private final Label songIdLabel  = new Label("Id");
    private final Spinner<Integer> songIdSpinner = new Spinner<>(1, 999, 1);

    private final StringProperty songNameProperty = new SimpleStringProperty();
    private final Label songNameLabel = new Label("Name");
    private final TextField songNameTextField = new TextField();

    private final StringProperty performerProperty = new SimpleStringProperty();
    private final Label performerLabel = new Label("Performer");
    private final TextField performerTextField = new TextField();

    private final ObjectProperty<Integer> tempoProperty = new SimpleObjectProperty<>(0);
    private final Label tempoLabel = new Label("Tempo");
    private final Spinner<Integer> tempoSpinner = new Spinner<>(1, 999, 1);

    private final StringProperty scaleProperty = new SimpleStringProperty(ScaleSelector.scales[0]);
    private final Label scaleLabel = new Label("Scale");
    private final ScaleSelector scaleSelector = new ScaleSelector(false);

    private final StringProperty pitchProperty = new SimpleStringProperty(ScaleSelector.pitches[0]);
    private final ObjectProperty<Integer> beatsProperty = new SimpleObjectProperty<>(4);
    private final ObjectProperty<Integer> beatProperty = new SimpleObjectProperty<>(4);
    private final Label meterLabel = new Label("Meter");
    private final SignatureSelector signatureSelector = new SignatureSelector();

    private boolean muteListenerUpdating = false;

    private void fillDatasources(Song song) {
        songIdProperty.set(song.getId());
        songNameProperty.set(song.getName());
        performerProperty.set(song.getSongInfo().getPerformer());
        tempoProperty.set(song.getSongInfo().getTempo());
        scaleProperty.set(song.getSongInfo().getScale().getScale());
        pitchProperty.set(song.getSongInfo().getScale().getPitch());
        beatsProperty.set(song.getSongInfo().getSignature().getBeats());
        beatProperty.set(song.getSongInfo().getSignature().getBeat());

        songIdProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.setId(newVal);
                checkChanges();
            }
        });

        songNameProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.setName(newVal);
                getPlaylistDetailsPane().getSongTitlePane().updateSongTitle(newVal);
                checkChanges();
            }
        });

        performerProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().setPerformer(newVal);
                checkChanges();
            }
        });

        tempoProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().setTempo(newVal);
                checkChanges();
            }
        });

        scaleProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().getScale().setScale(newVal);
                checkChanges();
            }
        });

        pitchProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().getScale().setPitch(newVal);
                checkChanges();
            }
        });

        beatProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().getSignature().setBeat(newVal);
                checkChanges();
            }
        });

        beatsProperty.addListener((obs, oldVal, newVal) -> {
            if (! muteListenerUpdating) {
                workingSong.getSongInfo().getSignature().setBeats(newVal);
                checkChanges();
            }
        });
    }

    private void addComponents() {
        getChildren().clear();

        int row = 0;
        add(songIdLabel, 0, row, 1, 1);
        add(songIdSpinner, 2, row, 1, 1);
        songIdSpinner.getValueFactory().valueProperty().bindBidirectional(songIdProperty);
        songIdSpinner.setEditable(true);

        row++;
        add(songNameLabel, 0, row, 1, 1);
        add(songNameTextField, 2, row, 4, 1);
        songNameTextField.textProperty().bindBidirectional(songNameProperty);

        row++;
        add(performerLabel, 0, row, 1, 1);
        add(performerTextField, 2, row, 4, 1);
        performerTextField.textProperty().bindBidirectional(performerProperty);

        row++;
        add(tempoLabel, 0, row, 1, 1);
        add(tempoSpinner, 2, row, 1, 1);
        tempoSpinner.getValueFactory().valueProperty().bindBidirectional(tempoProperty);
        tempoSpinner.setEditable(true);

        tempoSpinner.getEditor().textProperty().addListener((observable, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                try {
                    StringConverter<Integer> converter = ((SpinnerValueFactory<Integer>) tempoSpinner.getValueFactory()).getConverter();
                    Integer value = converter.fromString(newText);
                    tempoSpinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                }
            }
        });
/*        tempoSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Tempo changed to " + newVal);
        }); */

        row++;
        add(scaleLabel, 0, row, 1, 1);
        add(scaleSelector, 2, row, 6, 1);
        scaleSelector.scaleProperty.bindBidirectional(scaleProperty);
        scaleSelector.pitchProperty.bindBidirectional(pitchProperty);

        row++;
        add(meterLabel, 0, row, 1, 1);
        add(signatureSelector, 2, row, 3, 1);
        signatureSelector.beatProperty.bindBidirectional(beatProperty);
        signatureSelector.bpmProperty.bindBidirectional(beatsProperty);
    }

    private Song workingSong;
    public void setSong(Song song, boolean fromMappingPane) {
        muteListenerUpdating = true;

        if (song != null) {
            workingSong = song;

            Song deepCopy = SerializationUtils.clone(song);
            AppInfo.INSTANCE.setCurrentSong(deepCopy);

            if (! fromMappingPane) {
                getPlaylistDetailsPane().getSongMappingsPane().setSong(song);
            }

            getPlaylistDetailsPane().getSongTitlePane().updateSongTitle(song.getName());
            fillDatasources(song);
            addComponents();
        } else {
            workingSong = null;
            AppInfo.INSTANCE.setCurrentSong(null);
            getPlaylistDetailsPane().getSongMappingsPane().setSong(null);
            getPlaylistDetailsPane().getSongTitlePane().updateSongTitle("");
        }

        muteListenerUpdating = false;
    }

    private void checkChanges() {
        if (AppInfo.INSTANCE.getCurrentSong() != null) {
            boolean originalEmpty = AppInfo.INSTANCE.getCurrentSong().isEmpty();
            boolean currentEmpty = workingSong.isEmpty();

            if (originalEmpty) {
                if (! currentEmpty) {
                    if (! workingSong.getName().isEmpty()) {
                        getPlaylistDetailsPane().getPlaylistPane().changeState(States.ADD);
                    }
                } else {
                    getPlaylistDetailsPane().getPlaylistPane().changeState(States.NEW);
                }
            } else {
                boolean equal = AppInfo.INSTANCE.getCurrentSong().equals(workingSong);
                if (!equal) {
                    if (!workingSong.getName().isEmpty()) {
                        getPlaylistDetailsPane().getPlaylistPane().changeState(States.UPDATE);
                    }
                } else {
                    getPlaylistDetailsPane().getPlaylistPane().changeState(States.SELECT);
                }
            }
        }
    }

    public Song getSong() {
        return workingSong;
    }

    public void replaceWorkingSong(Song song) {
        workingSong = song;
        checkChanges();
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
