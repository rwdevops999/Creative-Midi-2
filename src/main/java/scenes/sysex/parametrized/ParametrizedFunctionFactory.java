package scenes.sysex.parametrized;

import entity.NameEntity;
import fx.dialog.DialogFactory;
import lombok.Getter;
import tools.jackson.core.util.Named;

public class ParametrizedFunctionFactory {
    @Getter
    private static final ParametrizedFunctionFactory instance = new ParametrizedFunctionFactory();

    private ParametrizedFunctionFactory () {
    }

    public NameEntity getParametrizedFunction(String name) {
        return ParametrizedFunctions.getParaFunctions().stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
