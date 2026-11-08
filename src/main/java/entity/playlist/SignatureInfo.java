package entity.playlist;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SignatureInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // beats / beat (e.g. 3/4)
    private Integer beats;
    private Integer beat;

    public SignatureInfo() {
        this.beats = 4;
        this.beat = 4;
    }

    public SignatureInfo(SignatureInfo other) {
        this();

        this.beats = other.getBeats();
        this.beat = other.getBeat();
    }

    @Override
    public String toString() {
        return this.beats + "/" + this.beat;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SignatureInfo other)) {
            return false;
        }

        return Objects.equals(this.beats, other.beats) &&
                Objects.equals(this.beat, other.beat)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beats, beat);
    }
}
