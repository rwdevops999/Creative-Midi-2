package util.keyboards.yamaha.entity;

import entity.NameEntity;

/**
 * Contains file information
 * - the file name (any name with extension)
 */
public class KbFile extends NameEntity {
    public KbFile(String name) {
        this.name = name;
    }
}
