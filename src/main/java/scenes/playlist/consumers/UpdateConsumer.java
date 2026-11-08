package scenes.playlist.consumers;

import entity.NameEntity;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class UpdateConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConsumer.class);

    public UpdateConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
/*        Sysex sysex = new Sysex((Sysex) entity);
        if (sysex.getName().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to add a sysex with empty name");
        } else {
            SysexContainer.getInstance().addSysex(sysex);

            SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
            SysexPane sysexPane = sceneActionsPane.getSysexPane();
            sysexPane.getSysexListPane().reload(sysex);
        } */
    }
}
