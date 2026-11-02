package entity.sysex;

import entity.NameEntity;
import lombok.Getter;

import java.util.Objects;

public class Sysex extends NameEntity {
    @Getter
    private SysexContent content;

    public Sysex() {
        super();
        this.content = new SysexContent();
    }

    public Sysex(String name) {
        this();

        this.name = name;
    }

    public Sysex(Sysex other) {
        this();

        this.name = other.name;
        this.content = new SysexContent(other.content);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sysex other)) {
            return false;
        }

        return Objects.equals(this.name, other.name) &&
               Objects.equals(this.content, other.content);    }

    @Override
    public int hashCode() {
        return Objects.hash(name, content);
    }

    public boolean isEmpty() {
        return (name == null || name.isEmpty()) && (content.getSysex() == null || content.getSysex().isEmpty());
    }
}
