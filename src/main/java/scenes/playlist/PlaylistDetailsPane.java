package scenes.playlist;

import entity.playlist.Song;
import entity.sysex.Sysex;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import util.app.AppInfo;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class PlaylistDetailsPane extends BorderPane {
    public PlaylistDetailsPane() {
        super();

        setId("PlaylistDetailsPane");
    }

    PlaylistPane parent;
    public PlaylistDetailsPane(PlaylistPane owner, double width, double height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showSmallPaneBorder(this, getColor("border", "green"));

        setPaneBackground(this);

        buildPane();
    }

    private void buildPane() {
        setTop(new SongTitlePane(this, 5.0));
        setCenter(new SongDetailsPane(this, 45.0));
        setBottom(new SongMappingsPane(this, 50.0));
    }

    // ACCESSORS
    public PlaylistPane getPlaylistPane() {
        return parent;
    }

    public SongTitlePane getSongTitlePane() {
        return (SongTitlePane) getTop();
    }

    public SongDetailsPane getSongDetailsPane() {
        return (SongDetailsPane) getCenter();
    }

    public SongMappingsPane getSongMappingsPane() {
        return (SongMappingsPane) getBottom();
    }
}
