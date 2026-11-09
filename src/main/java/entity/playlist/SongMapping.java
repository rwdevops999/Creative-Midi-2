package entity.playlist;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import entity.sysex.Sysex;
import lombok.Getter;
import lombok.Setter;
import util.convertors.SysexListToStringListConverter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SongMapping implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(converter = SysexListToStringListConverter.class)
    private Sysex receive;
    @JsonSerialize(converter = SysexListToStringListConverter.class)
    private Sysex reply;

    public SongMapping(Sysex receive, Sysex reply) {
        this.receive = receive;
        this.reply = reply;
    }

    @Override
    public String toString() {
        return this.receive.getName() + " -> " + this.reply.getName();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SongMapping other)) {
            return false;
        }

        return Objects.equals(this.receive, other.receive) &&
                Objects.equals(this.reply, other.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receive, reply);
    }
}
