package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexListPane;
import scenes.sysex.SysexPane;
import scenes.sysex.statemachine.States;

import java.util.function.BiConsumer;

public class NewConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(NewConsumer.class);

    public NewConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        Sysex sysex = new Sysex();

        SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
        SysexPane sysexPane = sceneActionsPane.getSysexPane();

        sysexPane.getSysexDetailsPane().setSysex(sysex);
        sysexPane.changeState(States.NEW);
    }
}
