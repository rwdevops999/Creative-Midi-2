package entity.playlist;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SongMapping implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String receive;
    private String reply;

    public SongMapping(String receive, String reply) {
        this.receive = receive;
        this.reply = reply;
    }

    public SongMapping() {
        this("", "");
    }

    @Override
    public String toString() {
        return this.receive + " -> " + this.reply;
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
