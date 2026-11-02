package scenes.eventlist.filter;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterVoicePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterVoicePane.class);

    public FilterVoicePane() {
        super();

        setId("FilterVoicePane");
    }

    private EventListFilterPane parent;
    public FilterVoicePane(EventListFilterPane owner) {
        this();

        showSmallPaneBorder(this, getColor("border", "blue"));

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {

    }
}
