package scenes.sysex.consumers;

import entity.NameEntity;
import entity.sysex.Sysex;
import fx.dialog.DialogFactory;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class ExecuteConsumer<T extends NameEntity, P extends Pane> extends NameEntity implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteConsumer.class);

    public ExecuteConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("Handling execute sysex");

        Sysex sysex = (Sysex) entity;
        if (sysex.getContent().getSysex().isEmpty()) {
            DialogFactory.getInstance().renderErrorDialog("Unable to execute an empty sysex");
        } else {
            System.out.println("Executing sysex: " + sysex.getContent().getSysex());
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
