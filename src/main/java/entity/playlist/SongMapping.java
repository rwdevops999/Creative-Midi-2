package entity.playlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import entity.sysex.Sysex;
import lombok.Getter;
import lombok.Setter;
import util.convertors.StringToSysexConverter;
import util.convertors.SysexListToStringListConverter;
import util.convertors.SysexToStringConverter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SongMapping implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(converter = SysexToStringConverter.class)
    @JsonDeserialize(converter = StringToSysexConverter.class)
    private Sysex receive;

    @JsonSerialize(converter = SysexToStringConverter.class)
    @JsonDeserialize(converter = StringToSysexConverter.class)
    private Sysex reply;

    public SongMapping() {
        this.receive = null;
        this.reply = null;
    }

    public SongMapping(Sysex receive, Sysex reply) {
        this();

        this.receive = receive;
        this.reply = reply;
    }

    public SongMapping(SongMapping other) {
        this();

        if (other.getReceive() != null) {
            this.receive = other.getReceive();
        }
        if (other.getReply() != null) {
            this.reply = other.getReply();
        }
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
