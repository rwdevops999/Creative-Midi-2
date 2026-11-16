package util.keyboards.yamaha.entity;

import entity.NameEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DriveEntity extends NameEntity {
    @Getter
    protected int id;

    @Getter
    protected List<KbFolder> folders;

    @Getter
    protected List<KbFile> files;

    public DriveEntity() {
        super();

        this.folders = new ArrayList<>();
        this.files = new ArrayList<>();
    }
}
