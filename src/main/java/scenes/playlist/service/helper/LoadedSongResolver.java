package scenes.playlist.service.helper;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class LoadedSongResolver {
    @Getter
    private static LoadedSongResolver instance = new LoadedSongResolver();

    private LoadedSongResolver() {
    }

    private static final String SongPrefix = "User:/";
    private static final String SongSuffix = ".S917.RGT";

    private static final int prefixLength = SongPrefix.length() * 2;
    private static final int suffixLength = SongSuffix.length() *2;

    public String resolveSongName(String hexSysexMessage) {
        String strippedString = hexSysexMessage.replaceAll("\\s", "");

        String input = strippedString.substring(20 + prefixLength, strippedString.length() - 4 - suffixLength);

        String songName = new String(HexFormat.of().parseHex(input), StandardCharsets.UTF_8);

        if (songName.endsWith("1") || songName.endsWith("2")) {
            songName = songName.substring(0, songName.length() - 1).trim();
        }

        return songName;
    }
}
