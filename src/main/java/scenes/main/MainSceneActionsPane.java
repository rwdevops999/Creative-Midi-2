package scenes.main;

import fx.component.SceneSelectButton;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListScene;
import scenes.playlist.PlaylistScene;
import scenes.sysex.SysexScene;
import scenes.voice.VoiceScene;
import util.app.AppInfo;
import util.app.scene.IScene;

@Getter
public class MainSceneActionsPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(MainSceneActionsPane.class);

    public MainSceneActionsPane() {
        super();

        setId("MainSceneActionsPane");

        int totalColumns = 10;
        double percentagePerColumn = 100.0 / totalColumns; // Dit is ~8.3333%

        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentagePerColumn);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        setHgap(10);
        setVgap(10);

        setPadding(new Insets(10));
    }

    // ACCESSORS
    private IScene parentScene;
    public MainSceneActionsPane(IScene ownerScene) {
        this();

        this.parentScene = ownerScene;

        buildPane();
    }

    private static final int ICON_SIZE=64;

    private void buildPane() {
        logger.debug("Building {}", getId());

        add(new SceneSelectButton(parentScene, ICON_SIZE, "sysexButton", "create sysex banks", "sysex", new SysexScene()), 1, 0);
        add(new SceneSelectButton(parentScene, ICON_SIZE, "eventListButton", "handle events", "event-list", new EventListScene()), 2, 0);
        add(new SceneSelectButton(parentScene, ICON_SIZE, "voiceButton", "search voices", "voice_search", new VoiceScene()), 3, 0);
        add(new SceneSelectButton(parentScene, ICON_SIZE, "playlistButton", "using the playlist", "playlist", new PlaylistScene()), 4, 0);

        if (AppInfo.INSTANCE.isTestMode()) {
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test1Button", "test 1", "test1", "Test1"), 3, 1);
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test2Button", "test 2", "test2", "Test2"), 4, 1);
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test3Button", "test 3", "test3", "Test3"), 5, 1);
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test4Button", "test 4", "test4", "Test4"), 6, 1);
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test5Button", "test 5", "test5", "Test5"), 7, 1);
            add(new SceneSelectButton(parentScene, ICON_SIZE, "test6Button", "test 6", "test6", "Test6"), 8, 1);
        }

        logger.debug("Built {}", getId());
    }

}
