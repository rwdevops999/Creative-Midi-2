package scenes.playlist;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class SongTitlePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SongTitlePane.class);

    public SongTitlePane() {
        super();

        setId("SongTitlePane");

        setPadding(new Insets(0, 0, 0, 5));
    }

    private PlaylistDetailsPane parent;
    public SongTitlePane(PlaylistDetailsPane owner, double height) {
        this();

        setPaneHeightAsPercentage(this, owner, height);
        showSmallPaneBorder(this, getColor("border", "blue"));

        parent = owner;

        buildPane();
    }

    private final StringProperty songTitleProperty = new SimpleStringProperty("");
    private void buildPane() {
        logger.debug("Building {}", getId());

        Label songTitle = new Label();
        songTitle.textProperty().bind(songTitleProperty);
        songTitle.setStyle(
                "-fx-text-fill: blue; " +
                        "-fx-font-weight: bold;"
        );
        getChildren().add(songTitle);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        getChildren().add(separator);

        logger.debug("Built {}", getId());
    }

    public void updateSongTitle(String songTitle) {
        songTitleProperty.setValue(songTitle);
    }

    // ACCESSORS
    public PlaylistDetailsPane getPlaylistDetailsPane() {
        return parent;
    }
}
