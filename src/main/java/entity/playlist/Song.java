package entity.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import entity.NameEntity;
import entity.sysex.Sysex;
import lombok.Getter;
import lombok.Setter;
import util.convertors.SysexListToStringListConverter;

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
    @JsonSerialize(converter = SysexListToStringListConverter.class)
    private List<Sysex> preMappings;
    private List<SongMapping> mappings;
    @JsonSerialize(converter = SysexListToStringListConverter.class)
    private List<Sysex> postMappings;

    public Song(Integer id) {
        this.id = id;
    }

    public Song() {
        this(1);

        this.name = "";

        this.songInfo = new SongInfo();
        this.mappings = new ArrayList<>();
        this.preMappings = new ArrayList<>();
        this.postMappings = new ArrayList<>();
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
                Objects.equals(this.mappings, other.mappings) &&
                Objects.equals(this.preMappings, other.preMappings) &&
                Objects.equals(this.postMappings, other.postMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, songInfo, mappings, preMappings, postMappings);
    }
}
