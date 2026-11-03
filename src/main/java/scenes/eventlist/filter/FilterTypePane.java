package scenes.eventlist.filter;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.filter.subpanes.MidiTypeFilterPane;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterTypePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterTypePane.class);

    public FilterTypePane() {
        super();

        setId("FilterTypePane");
    }

    private FilterTypePane pane;
    private EventListFilterPane parent;
    public FilterTypePane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        pane = this;

        buildPane();
    }

    private  void buildPane() {
        MidiTypeFilterPane midiTypeFilterPane = new MidiTypeFilterPane(this);
        getChildren().add(midiTypeFilterPane);
    }

    // ACCESSORS
    public EventListFilterPane getEventListFilterPane() {
        return parent;
    }
}
