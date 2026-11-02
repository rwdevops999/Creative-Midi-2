package scenes.sysex;

import entity.NameEntity;
import entity.sysex.Sysex;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.consumers.*;
import scenes.sysex.statemachine.StateMachine;
import scenes.sysex.statemachine.States;

import java.util.function.BiConsumer;

public class SysexPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(SysexPane.class);

    private StateMachine stateMachine = StateMachine.getInstance();

    public SysexPane() {
        super();

        setId("SysexPane");

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        BiConsumer<NameEntity, Pane>[] consumers = new BiConsumer[]{
                new NewConsumer<Sysex, Pane>("New"),
                new AddConsumer<Sysex, Pane>("Add"),
                new DeleteConsumer<Sysex, Pane>("Delete"),
                new UpdateConsumer<Sysex, Pane>("Update"),
                new ExecuteConsumer<Sysex, Pane>("Execute"),
        };

        SceneActionsPane actionsPane = new SceneActionsPane(this, 20, 100, consumers, this::getEntity);
        stateMachine.setActionsPane(actionsPane);

        setLeft(new SysexListPane(this, 30, 100));
        setCenter(new SysexDetailsPane(this, 50, 100));
        setRight(actionsPane);

        stateMachine.setState(States.IDLE);

        logger.debug("Built {}", getId());
    }

    public NameEntity getEntity() {
        return getSysexDetailsPane().getSysex();
    }

    public void changeState(States state) {
        stateMachine.setState(state);
    }

    // ACCESSORS
    public SysexListPane getSysexListPane() {
        return (SysexListPane) getLeft();
    }

    public SysexDetailsPane getSysexDetailsPane() {
        return (SysexDetailsPane) getCenter();
    }

    public SceneActionsPane getSceneActionsPane() {
        return (SceneActionsPane) getRight();
    }

}
