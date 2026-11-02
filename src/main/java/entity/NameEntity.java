package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public class NameEntity {
    @Getter
    @Setter
    protected String name = "";

    protected String textIcon = "";

    @JsonIgnore
    public String getTextIcon() {
        return textIcon;
    }

    protected String fontIcon = "";
    @JsonIgnore
    public String getFontIcon() {
        return fontIcon;
    }
}
