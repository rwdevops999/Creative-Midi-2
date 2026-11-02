package scenes.tests.panes;

import entity.NameEntity;
import entity.sysex.Sysex;
import fx.component.LightIndicatorButton;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.parametrized.Parameter;
import scenes.sysex.parametrized.ParametrizedFunctionFactory;
import scenes.sysex.parametrized.ParametrizedFunctions;

import java.util.function.Function;

import static util.Util.setPaneBackground;

public class Test3Pane extends HBox {
    public Test3Pane() {
        super();

        setId("Test3Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        SysexContainer.getInstance().load();
    }

    private void runTest() {
        Sysex tempoSysex = SysexContainer.getInstance().getSysexByName("Tempo");
        if (tempoSysex != null) {
            Function<Integer, Parameter> tempoFunction = ParametrizedFunctionFactory.getInstance().getParametrizedFunction("Tempo");
            Parameter result = tempoFunction.apply(120);

            String sysex = tempoSysex.getContent().getSysex().replace(result.getParameterString(), result.getReplacementString());
            System.out.println(sysex);
        }
    }
}
