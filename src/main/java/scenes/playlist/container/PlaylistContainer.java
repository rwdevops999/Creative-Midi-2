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
import util.properties.AppProperties;

import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static util.properties.PropertyKeys.PLAYLIST_DIR;
import static util.properties.PropertyKeys.PLAYLIST_FILENAME;

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

}
