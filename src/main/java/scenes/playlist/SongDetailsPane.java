package scenes.playlist;

import entity.playlist.Song;
import fx.component.ScaleSelector;
import fx.component.SignatureSelector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.playlist.container.PlaylistContainer;
import util.app.AppInfo;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SongDetailsPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(SongDetailsPane.class);

    public SongDetailsPane() {
        super();

        setId("SongDetailsPane");
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

    private final ObjectProperty<Integer> songIdProperty = new SimpleObjectProperty<>(PlaylistContainer.getInstance().getNextSongId());
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

    private void setDatasource(Song song) {
        songIdProperty.set(song.getId());
        songNameProperty.set(song.getName());
        performerProperty.set(song.getSongInfo().getPerformer());
        tempoProperty.set(song.getSongInfo().getTempo());
        scaleProperty.set(song.getSongInfo().getScale().getScale());
        pitchProperty.set(song.getSongInfo().getScale().getPitch());
        beatsProperty.set(song.getSongInfo().getSignature().getBeats());
        beatProperty.set(song.getSongInfo().getSignature().getBeat());
    }

    private void addComponents() {
        getChildren().clear();

        int row = 0;
        add(songIdLabel, 0, row, 1, 1);
        add(songIdSpinner, 2, row, 1, 1);

        row++;
        add(songNameLabel, 0, row, 1, 1);
        add(songNameTextField, 2, row, 4, 1);

        row++;
        add(performerLabel, 0, row, 1, 1);
        add(performerTextField, 2, row, 4, 1);

        row++;
        add(tempoLabel, 0, row, 1, 1);
        add(tempoSpinner, 2, row, 1, 1);

        row++;
        add(scaleLabel, 0, row, 1, 1);
        add(scaleSelector, 2, row, 6, 1);

        row++;
        add(meterLabel, 0, row, 1, 1);
        add(signatureSelector, 2, row, 3, 1);

    }

    private Song workingSong;
    public void setSong(Song song) {
        if (song != null) {
            Song deepCopiedSong = SerializationUtils.clone(song);
            AppInfo.INSTANCE.setCurrentSong(deepCopiedSong);

            workingSong = song;

            getPlaylistDetailsPane().getSongMappingsPane().setSong(workingSong);

            getPlaylistDetailsPane().getSongTitlePane().updateSongTitle(song.getName());

            setDatasource(song);

            buildPane();
            addComponents();

/*           getPlaylistDetailsPane().getSongMappingsPane().buildPane(song.getMappings()); */
        } else {
            workingSong = null;
            getPlaylistDetailsPane().getSongTitlePane().updateSongTitle("");
            getChildren().clear();
        }
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
