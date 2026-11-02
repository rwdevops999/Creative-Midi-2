package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;

import java.util.function.BiConsumer;

public class DeleteConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteConsumer.class);

    public DeleteConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("Handling delete sysex");

        Sysex sysex = (Sysex) entity;
        SysexContainer.getInstance().deleteSysex(sysex);

        SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
        SysexPane sysexPane = sceneActionsPane.getSysexPane();
        sysexPane.getSysexListPane().reload(null);
    }
}
