package entity.voice;

import entity.NameEntity;

public class Patch extends NameEntity {
    public Patch() {
        this.name = "";
        this.textIcon = "";
    }

    public Patch(String name) {
        this();

        this.name = name;
    }
}
