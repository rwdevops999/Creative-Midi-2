package scenes.sysex.consumers;

import entity.NameEntity;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class AddConsumer<T extends NameEntity, P extends Pane> implements BiConsumer<T, P> {
    private static final Logger logger = LoggerFactory.getLogger(AddConsumer.class);

    @Getter
    private String name;

    public AddConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(T entity, P pane) {
        logger.debug("Handling add sysex");
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
