package entity;

import java.util.Objects;

public class NoteEntity extends NameEntity{
    private int id;

    public NoteEntity() {
        super();
    }

    public NoteEntity(int id, String name) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NoteEntity other = (NoteEntity) obj;

        return (this.id == other.id && this.name.equalsIgnoreCase(other.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
