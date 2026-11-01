package scenes.sysex;

import entity.NameEntity;
import entity.sysex.Sysex;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.consumers.*;

import java.util.function.BiConsumer;

public class SysexPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(SysexPane.class);

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

        setLeft(new SysexListPane(this, 30, 100));
        setCenter(new SysexDetailsPane(this, 50, 100));
        setRight(new SceneActionsPane(this, 20, 100, consumers, this::getEntity));

        logger.debug("Built {}", getId());
    }

    public NameEntity getEntity() {
//        return getSysexDetailsPane().getSysex();
        return null;
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
