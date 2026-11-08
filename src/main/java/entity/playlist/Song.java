package entity.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

        this.songInfo = new SongInfo();
        this.mappings = new ArrayList<>();
    }

    public Song(Song other) {
        this();

        this.id = other.getId();
        this.name = other.getName();
        this.songInfo = new SongInfo(other.getSongInfo());
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (name.isEmpty() && songInfo.isEmpty() && mappings.isEmpty());
    }
}
