package app.root.root.header;

import fx.component.LabeledSelector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;

public class SelectorsPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(SelectorsPane.class);

    public SelectorsPane() {
        super();

        setId("SelectorsPane");

        setSpacing(0);
        setAlignment(Pos.TOP_LEFT);
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
        LabeledSelector deviceSelector = new LabeledSelector(parent, "Device");
        // Tell the HBox to distribute its space to this child
        HBox.setHgrow(deviceSelector, Priority.ALWAYS);
        getChildren().add(deviceSelector);

        LabeledSelector keyboardSelector = new LabeledSelector(parent, "Keyboard");
        // Tell the HBox to distribute its space to this child too (creating a 50/50 split)
        HBox.setHgrow(keyboardSelector, Priority.ALWAYS);
        getChildren().add(keyboardSelector);
    }

    // ACCESSORS
    public HeaderPane getHeaderPane() {
        return this.parent;
    }
}
