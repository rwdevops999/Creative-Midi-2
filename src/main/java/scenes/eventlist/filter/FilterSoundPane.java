package scenes.eventlist.filter;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterSoundPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterSoundPane.class);

    public FilterSoundPane() {
        super();

        setId("FilterSoundPane");
    }

    private EventListFilterPane parent;
    public FilterSoundPane(EventListFilterPane owner) {
        this();

        showSmallPaneBorder(this, getColor("border", "orange"));

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {

    }
}
