package entity.voice;

import entity.NameEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Group extends NameEntity implements Serializable {
    private static final long serialVersionUID = 1L; //

    private Integer index;
    private Group parent;
    private List<Group> groups = new ArrayList<>();
    private List<Patch> patches = new ArrayList<>();

    public Group() {
    }

    public Group (Integer index, String name, Group parent) {
        this();

        this.name = name;
        this.index = index;
        this.parent = parent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('[').append(this.name).append(']');
        if (!this.getGroups().isEmpty()) {
            sb.append(' ').append(this.getGroups());
        }

        return sb.toString();
    }
}
