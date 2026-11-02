package scenes.eventlist.filter;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;
import scenes.eventlist.editor.EventListEditorPane;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterTitlePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterTitlePane.class);

    public FilterTitlePane() {
        super();

        setId("FilterTitlePane");
    }

    private EventListFilterPane parent;
    public FilterTitlePane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {

    }
}
