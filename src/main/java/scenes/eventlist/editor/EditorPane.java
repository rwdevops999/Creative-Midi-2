package scenes.eventlist.editor;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class EditorPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(EditorPane.class);

    public EditorPane() {
        super();

        setId("EditorPane");
    }

    private EventListEditorPane parent;
    public EditorPane(EventListEditorPane owner, int height) {
        this();

        this.parent = owner;

        setPaneHeightAsPercentage(this, owner, height);

        buildPane();
    }

    private  void buildPane() {

    }
}
