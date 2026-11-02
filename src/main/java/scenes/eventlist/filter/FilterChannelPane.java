package scenes.eventlist.filter;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.filter.subpanes.ChannelFilterPane;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterChannelPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterChannelPane.class);

    public FilterChannelPane() {
        super();

        setId("FilterChannelPane");
    }

    private EventListFilterPane parent;
    public FilterChannelPane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {
        ChannelFilterPane channelFilterPane = new ChannelFilterPane(this);
        getChildren().add(channelFilterPane);
    }
}
