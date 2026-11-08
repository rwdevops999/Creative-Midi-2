package scenes.playlist;

import entity.playlist.Song;
import entity.playlist.SongMapping;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SongMappingsPane extends StackPane {
    private static final Logger logger = LoggerFactory.getLogger(SongMappingsPane.class);

    public SongMappingsPane() {
        super();

        setId("SongMappingsPane");
    }

    private PlaylistDetailsPane parent;

    public SongMappingsPane(PlaylistDetailsPane owner, double height) {
        this();

        parent = owner;

        setPaneHeightAsPercentage(this, owner, height);
        showSmallPaneBorder(this, getColor("border", "turquoise"));

        buildPane(new ArrayList<>());
    }

    public void buildPane(List<SongMapping> mappings) {
        logger.debug("Building {}", getId());

        logger.debug("Built {}", getId());
    }

    public void setSong(Song song) {
        if (song != null) {
            // TODO handle this
/*            if (table != null) {
                ObservableList<Mapping> tableData = FXCollections.observableArrayList(new ArrayList<>(song.getMappings()));
                mappingProperty.set(tableData);
                currentMappings = song.getMappings();
            } */
        }
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
