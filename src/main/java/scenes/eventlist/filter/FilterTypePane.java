package scenes.eventlist.filter;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterTypePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterTypePane.class);

    public FilterTypePane() {
        super();

        setId("FilterTypePane");
    }

    private EventListFilterPane parent;
    public FilterTypePane(EventListFilterPane owner) {
        this();

        showSmallPaneBorder(this, getColor("border", "green"));

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {

    }
}
