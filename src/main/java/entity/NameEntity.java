package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class NameEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
