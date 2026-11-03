package scenes.eventlist.filter;

import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;

import static util.app.colors.ColorScheme.getColor;

public class EventListFilterPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(EventListFilterPane.class);

    public EventListFilterPane() {
        super();

        setId("EventListFilterPane");
    }

    private EventListPane parent;
    public EventListFilterPane(EventListPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private FilterChannelPane filterChannelPane;

    private void buildPane() {
        logger.debug("Building {}", getId());

        // 1. Clear eventual old children en constraints on this GridPane
        getChildren().clear();
        getRowConstraints().clear();
        getColumnConstraints().clear();

        // 2. Create the sub-panes
        FilterTitlePane filterTitlePane = new FilterTitlePane(this);
        FilterTypePane filterTypePane = new FilterTypePane(this);
        filterChannelPane = new FilterChannelPane(this);
        FilterChannelControlPane filterCCPane = new FilterChannelControlPane(this);

        // 3. Set the row percentages
        RowConstraints rowTitle = new RowConstraints();
        rowTitle.setPercentHeight(10);
        rowTitle.setFillHeight(true);

        RowConstraints rowType = new RowConstraints();
        rowType.setPercentHeight(30);
        rowType.setFillHeight(true);

        RowConstraints rowChannel = new RowConstraints();
        rowChannel.setPercentHeight(30);
        rowChannel.setFillHeight(true);

        RowConstraints rowCC = new RowConstraints();
        rowCC.setPercentHeight(20);
        rowCC.setFillHeight(true);

        this.getRowConstraints().addAll(rowTitle, rowType, rowChannel, rowCC);

        // 4. Fill the column completly
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);
        this.getColumnConstraints().add(col);

        // 5. Add the panes to the GridPane
        this.add(filterTitlePane, 0, 0);
        this.add(filterTypePane, 0, 1);
        this.add(filterChannelPane, 0, 2);
        this.add(filterCCPane, 0, 3);

        logger.debug("Built {}", getId());
    }

     // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }

    public FilterChannelPane getFilterChannelPane() {
        return filterChannelPane;
    }
}
