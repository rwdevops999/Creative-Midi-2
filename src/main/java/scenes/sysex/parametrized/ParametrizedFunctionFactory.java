package scenes.sysex.parametrized;

import entity.NameEntity;
import fx.dialog.DialogFactory;
import lombok.Getter;
import tools.jackson.core.util.Named;

import java.util.function.Function;

public class ParametrizedFunctionFactory {
    @Getter
    private static final ParametrizedFunctionFactory instance = new ParametrizedFunctionFactory();

    private ParametrizedFunctionFactory () {
    }

    public Function<Integer, Parameter> getParametrizedFunction(String name) {
        return (Function<Integer, Parameter>)ParametrizedFunctions.getParaFunctions().stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
