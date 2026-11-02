package scenes.sysex.parametrized;

import lombok.Getter;

@Getter
public class Parameter {
    String parameterString;
    String replacementString;

    public Parameter(String parameterString, String replacementString) {
        this.parameterString = parameterString;
        this.replacementString = replacementString;
    }
}
