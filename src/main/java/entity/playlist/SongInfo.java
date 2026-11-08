package entity.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SongInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String performer;
    private Integer tempo;
    private ScaleInfo scale;
    private SignatureInfo signature;

    public SongInfo() {
        this.performer = "";
        this.tempo = 120;
        this.scale = new ScaleInfo();
        this.signature = new SignatureInfo();
    }

    public SongInfo(SongInfo other) {
        this();

        this.performer = other.getPerformer();
        this.tempo = other.getTempo();
        this.scale = new ScaleInfo(other.getScale());
        this.signature = new SignatureInfo(other.getSignature());
    }

    @Override
    public String toString() {
        return
                "[" + System.lineSeparator() +
                        " - PERFORMER: " + this.performer + System.lineSeparator() +
                        " - TEMPO: " + this.tempo + " BPM" + System.lineSeparator() +
                        " - SCALE: " + this.scale.toString() + System.lineSeparator() +
                        " - SIGNATURE: " + this.signature.toString() + System.lineSeparator() +
                        "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SongInfo other)) {
            return false;
        }

        return Objects.equals(this.performer, other.performer) &&
                Objects.equals(this.tempo, other.tempo) &&
                this.scale.equals(other.scale) &&
                this.signature.equals(other.signature)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(performer, tempo, scale, signature);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return performer.isEmpty();
    }
}
