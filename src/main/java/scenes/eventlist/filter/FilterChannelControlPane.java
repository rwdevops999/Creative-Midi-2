package scenes.eventlist.filter;

import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.filter.subpanes.ChannelControlPane;

public class FilterChannelControlPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterChannelControlPane.class);

    public FilterChannelControlPane() {
        super();

        setId("FilterVoicePane");
    }

    private EventListFilterPane parent;
    public FilterChannelControlPane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {
        ChannelControlPane channelControlPane = new ChannelControlPane(this);
        getChildren().add(channelControlPane);
    }

    // ACCESSOR
    public EventListFilterPane getEventListFilterPane() {
        return parent;
    }
}
