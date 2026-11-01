package entity.sysex;

import entity.NameEntity;

import java.util.Objects;

public class Sysex extends NameEntity {
    private SysexContent content;

    public Sysex() {
        super();
    }

    public Sysex(String name) {
        this();

        this.name = name;
    }

    public Sysex(Sysex other) {
        this();

        this.name = other.name;
        this.content = other.content;
    }

    public void setContent(SysexContent content) {
        this.content = content;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sysex other)) {
            return false;
        }

        return Objects.equals(this.name, other.name) && Objects.deepEquals(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, content);
    }
}
