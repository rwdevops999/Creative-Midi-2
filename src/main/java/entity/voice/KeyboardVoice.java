package entity.voice;

import entity.NameEntity;
import lombok.Getter;
import util.voice.VoiceType;

import java.util.Objects;

public class KeyboardVoice extends NameEntity {
    @Getter
    private VoiceType voiceType;

    public KeyboardVoice() {
        super();

        this.name = "";
        this.textIcon = "";

        this.voiceType = null;
        this.fontIcon = "";
    }

    public KeyboardVoice(String name, String voiceType) {
        this();

        this.name = name;

        this.voiceType = "INS".equalsIgnoreCase(voiceType) ? VoiceType.INS : VoiceType.MS2S;
        this.fontIcon = "INS".equalsIgnoreCase(voiceType) ? "bd1-beauty-instrument" : "si-yamahacorporation";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyboardVoice synchronizedVoice = (KeyboardVoice) o;

        // compare here all fields that make the voice file unique
        return Objects.equals(this.getName(), synchronizedVoice.getName()) &&
                Objects.equals(this.getTextIcon(), synchronizedVoice.getTextIcon()) &&
                Objects.equals(this.getFontIcon(), synchronizedVoice.getFontIcon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTextIcon(), getFontIcon());
    }

    @Override
    public String toString() {
        return name + (VoiceType.INS.equals(voiceType) ? ".ins" : ".txt");
    }
}
