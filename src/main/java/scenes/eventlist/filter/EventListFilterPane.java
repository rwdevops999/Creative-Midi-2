package scenes.eventlist.filter;

import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showSmallPaneBorder;
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

    private void buildPane() {
        logger.debug("Building {}", getId());

        // 1. Wis eventuele oude children en restricties van DEZE GridPane
        getChildren().clear();
        getRowConstraints().clear();
        getColumnConstraints().clear();

        // 2. Maak de sub-panes aan
        FilterTitlePane filterTitlePane = new FilterTitlePane(this);
        FilterTypePane filterTypePane = new FilterTypePane(this);
        FilterVoicePane filterVoicePane = new FilterVoicePane(this);
        FilterChannelPane filterChannelPane = new FilterChannelPane(this);
        FilterSoundPane filterSoundPane = new FilterSoundPane(this);

        // 3. Definieer de rij-percentages RECHTSTREEKS op deze pane
        RowConstraints rowTitle = new RowConstraints();
        rowTitle.setPercentHeight(12);
        rowTitle.setFillHeight(true);

        RowConstraints rowType = new RowConstraints();
        rowType.setPercentHeight(28);
        rowType.setFillHeight(true);

        RowConstraints rowVoice = new RowConstraints();
        rowVoice.setPercentHeight(30);
        rowVoice.setFillHeight(true);

        RowConstraints rowChannel = new RowConstraints();
        rowChannel.setPercentHeight(20);
        rowChannel.setFillHeight(true);

        RowConstraints rowSound = new RowConstraints();
        rowSound.setPercentHeight(10);
        rowSound.setFillHeight(true);

        this.getRowConstraints().addAll(rowTitle, rowType, rowVoice, rowChannel, rowSound);

        // 4. Zorg dat de kolom de volledige breedte vult
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);
        this.getColumnConstraints().add(col);

        // 5. Voeg de panes toe aan DEZE GridPane (node, columnIndex, rowIndex)
        this.add(filterTitlePane, 0, 0);
        this.add(filterTypePane, 0, 1);
        this.add(filterVoicePane, 0, 2);
        this.add(filterChannelPane, 0, 3);
        this.add(filterSoundPane, 0, 4);

        // 6. Optioneel: Dwing de subpanes om vertikaal mee te groeien binnen hun cel
        GridPane.setVgrow(filterTitlePane, Priority.ALWAYS);
        GridPane.setVgrow(filterTypePane, Priority.ALWAYS);
        GridPane.setVgrow(filterVoicePane, Priority.ALWAYS);
        GridPane.setVgrow(filterChannelPane, Priority.ALWAYS);
        GridPane.setVgrow(filterSoundPane, Priority.ALWAYS);

        logger.debug("Built {}", getId());
    }

     // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }
}
