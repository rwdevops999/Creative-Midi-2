package scenes.sysex.parametrized;

import entity.NameEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ParametrizedFunctions {
    @Getter
    private static List<NameEntity> paraFunctions = new ArrayList<>();

    static {
        paraFunctions.add(new TempoFunction("Tempo"));
    }
}
