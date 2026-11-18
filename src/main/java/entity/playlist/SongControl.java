package entity.playlist;

import entity.sysex.Sysex;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongControl {
    @Getter
    private String songName;

    @Getter
    @Setter
    private List<Sysex> preMappings = new ArrayList<>();

    @Getter
    @Setter
    private List<Sysex> postMappings = new ArrayList<>();

//    private Map<ByteBuffer, ByteBuffer> mappingsMap = new HashMap<>();
//    private Map<ByteBuffer, String> mappingsNameMap = new HashMap<>();

    public SongControl() {
        this.songName = "";
    }

    public SongControl(String name) {
        this.songName = name;
    }
}
