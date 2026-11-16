package util.keyboards.yamaha.entity;

import entity.NameEntity;
import lombok.Getter;

public class CommEntity extends NameEntity {
    @Getter
    public String representation = "";

    public CommEntity() {
        this.name = "";
    }

    public CommEntity(String name) {
        this.name = name;
    }

    public CommEntity(String name, String sysex) {
        this.name = name;
        this.representation = sysex;
    }
}
