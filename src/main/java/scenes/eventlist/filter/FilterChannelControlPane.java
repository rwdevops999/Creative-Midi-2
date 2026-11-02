package scenes.eventlist.filter;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.tests.panes.ChannelControlPane;

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
}
