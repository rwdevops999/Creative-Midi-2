package entity.playlist;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ScaleInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String scale;
    private String pitch;

    public ScaleInfo() {
        this.scale = "C";
        this.pitch = "major";
    }

    public ScaleInfo(ScaleInfo other) {
        this.scale = other.getScale();
        this.pitch = other.getPitch();
    }

    @Override
    public String toString() {
        return this.scale + (this.pitch.equals("minor") ? "m" : "");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScaleInfo other)) {
            return false;
        }

        return Objects.equals(this.scale, other.scale) &&
                Objects.equals(this.pitch, other.pitch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scale, pitch);
    }

}
