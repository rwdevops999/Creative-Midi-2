package entity.playlist;

import entity.NameEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Song extends NameEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private SongInfo songInfo;
    private List<SongMapping> mappings;

    public Song(Integer id) {
        this.id = id;
    }

    public Song() {
        this(1);

        this.name = "";
    }

    public Song(Song other) {
        this.id = other.getId();
        this.name = other.getName();
        this.songInfo = new SongInfo(other.getSongInfo());
        this.mappings = new ArrayList<>();
    }
}
