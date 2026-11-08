package entity.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import entity.NameEntity;
import entity.sysex.Sysex;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Song(String name) {
        this(1);

        this.name = name;
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

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Song other)) {
            return false;
        }

        return Objects.equals(this.name, other.name) &&
                Objects.equals(this.id, other.id) &&
                Objects.equals(this.songInfo, other.songInfo) &&
                Objects.equals(this.mappings, other.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, songInfo, mappings);
    }
}
