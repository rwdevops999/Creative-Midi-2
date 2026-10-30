package scenes.root.root.header;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.util.setPaneSizeAsPercentage;
import static util.util.showPaneBorder;

public class SelectorsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SelectorsPane.class);

    public SelectorsPane() {
        super();

        setId("SelectorsPane");
    }

    private HeaderPane parent;
    public SelectorsPane(HeaderPane owner, int width, int height) {
        this();

        parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "blue"));

        buildPane();
    }

    private void buildPane() {

    }

    // ACCESSORS
    public HeaderPane getHeaderPane() {
        return this.parent;
    }
}
