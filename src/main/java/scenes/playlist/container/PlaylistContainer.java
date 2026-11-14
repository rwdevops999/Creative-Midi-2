package scenes.playlist.container;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import entity.playlist.Song;
import entity.sysex.Sysex;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.container.SysexContainer;
import util.AppNotifications;
import util.properties.AppProperties;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static util.properties.PropertyKeys.*;
import static util.properties.PropertyKeys.SYSEX_FILENAME;

public class PlaylistContainer {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistContainer.class);

    @Getter
    private static PlaylistContainer instance = new PlaylistContainer();

    private final ObjectMapper mapper;

    @Getter
    private List<Song> playList;

    private PlaylistContainer() {
        mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // INGEBOUWD & VEILIG: Verwijdert alle nulls en lege strings ("") globally!
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY);

        playList = new ArrayList<>();
    }

    @Getter
    private boolean fileLoaded = false;

    public void loadPlaylist() {
        String playlistPath = AppProperties.getInstance().getPropertyAsString(PLAYLIST_DIR, "./data/playlist");
        String filename = AppProperties.getInstance().getPropertyAsString(PLAYLIST_FILENAME, "playlist.json");

        String playlistFile = playlistPath + "/" + filename;

        logger.debug("Loading the playlist from file {}", playlistFile);
        try (InputStream in = Files.newInputStream(Path.of(playlistFile))) {
            playList = mapper.readValue(in, new TypeReference<>() {});
            fileLoaded = true;
        } catch (IOException ioe) {
            logger.error("Exception. CAUSE: {}", ioe.getMessage());
        }
    }

    public Integer getNextSongId() {
        Song found = playList.stream().max(Comparator.comparing(Song::getId)).orElse(null);

        if (found != null) {
            return found.getId()+1;
        }

        return 1;
    }

    private void export() {
        String playlistPath = AppProperties.getInstance().getPropertyAsString(PLAYLIST_DIR, "./data/playlist");
        String playlistFilename = AppProperties.getInstance().getPropertyAsString(PLAYLIST_FILENAME, "playlist.json");

        String fullPlaylistFilnamee = playlistPath + "/" + playlistFilename;

        ObjectMapper mapperExport = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        try {
            File file = new File(fullPlaylistFilnamee);
            mapperExport.writeValue(file, playList);
            AppNotifications.showNotification("Playlist export", "Playlist written to: " + fullPlaylistFilnamee);
        } catch (Exception e) {
            logger.error("Exception: CAUSE: {}", e.getMessage());
        }
    }


    public void addSong(Song song) {
        Song songInList = playList.stream().filter(s -> s.getName().equals(song.getName())).findFirst().orElse(null);
        if (songInList != null) {
            int index = playList.indexOf(songInList);
            playList.set(index, song);
        } else {
            playList.add(song);
        }

        export();
    }

    public void replace(Song originalSong, Song newSong) {
        Song oldSong = playList.stream().filter(s -> s.getName().equals(originalSong.getName())).findFirst().orElse(null);
        if (oldSong != null) {
            int index = playList.indexOf(oldSong);
            playList.set(index, newSong);
        }
        export();
    }

    public void delete(Song song) {
        Song oldSong = playList.stream().filter(s -> s.getName().equals(song.getName())).findFirst().orElse(null);
        if (oldSong != null) {
            playList.remove(oldSong);
            export();
        }
    }

    public boolean isEmpty() {
        return playList.isEmpty();
    }

    public boolean contains(String songName) {
        return playList.stream().anyMatch(s -> s.getName().equals(songName));
    }

    public Song getSongByName(String songName) {
        return playList.stream().filter(s -> s.getName().equals(songName)).findFirst().orElse(null);
    }
}
