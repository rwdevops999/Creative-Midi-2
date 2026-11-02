package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import entity.sysex.SysexContent;
import fx.dialog.DialogFactory;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexDetailsPane;
import scenes.sysex.SysexPane;
import scenes.sysex.container.SysexContainer;

import java.util.function.BiConsumer;

public class AddConsumer<T extends NameEntity, P extends Pane>  extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(AddConsumer.class);

    public AddConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("Handling add sysex");

        Sysex sysex = (Sysex) entity;
        if (sysex.getName().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to add a  sysex without name");
        } else {
            SysexContainer.getInstance().addSysex(sysex);

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
