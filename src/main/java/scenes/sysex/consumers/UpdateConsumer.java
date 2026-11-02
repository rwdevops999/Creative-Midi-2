package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;
import util.app.AppInfo;

import java.util.function.BiConsumer;

public class UpdateConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConsumer.class);

    public UpdateConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("Handling update sysex");

        Sysex sysex = new Sysex((Sysex) entity);

        if (sysex.getName().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to update a sysex with empty name");
        } else {
            Sysex originalSysex = AppInfo.INSTANCE.getSysex();
            SysexContainer.getInstance().replace(originalSysex, sysex);

            SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
            SysexPane sysexPane = sceneActionsPane.getSysexPane();
            sysexPane.getSysexListPane().reload(sysex);
        }
/*
        SceneActionsPane sceneActionsPane = (SceneActionsPane) pane;
        SysexPane sysexPane = sceneActionsPane.getSysexPane();

        Sysex sysex = new Sysex("Unknown");
        sysex.getList().add(new SysexContent(""));

        ApplicationInfo.getInstance().setCurrentSysex(sysex);

        SysexDetailsPane sysexDetailsPane = new SysexDetailsPane(sysexPane, sysex);
        sysexPane.setCenter(sysexDetailsPane);

        SysexStateMachine stateMachine = sysexPane.getSysexDetailsPane().getStateMachine();
        stateMachine.transitionTo(SysexState.NEW, true, sysexPane.getSysexDetailsPane().getSupplier()); */
    }
}
