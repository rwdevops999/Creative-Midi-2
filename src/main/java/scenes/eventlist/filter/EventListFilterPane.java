package scenes.eventlist.filter;

import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;

import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class EventListFilterPane extends VBox {
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

        // Maak de sub-panes aan
        FilterTitlePane filterTitlePane = new FilterTitlePane(this);
        FilterTypePane filterTypePane = new FilterTypePane(this);
        FilterVoicePane filterVoicePane = new FilterVoicePane(this);
        FilterChannelPane filterChannelPane = new FilterChannelPane(this);
        FilterSoundPane filterSoundPane = new FilterSoundPane(this);

        // Maak een interne GridPane aan die de percentages regelt
        GridPane gridLayout = new GridPane();

        // Zorg dat de GridPane de volledige breedte en hoogte van deze VBox gebruikt
        gridLayout.setMaxWidth(Double.MAX_VALUE);
        gridLayout.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(gridLayout, Priority.ALWAYS);

        // Definieer de rij-percentages (bijv. 20%, 50%, 30%)
        RowConstraints rowTitle = new RowConstraints();
        rowTitle.setPercentHeight(10);
        rowTitle.setFillHeight(true);

        RowConstraints rowType = new RowConstraints();
        rowType.setPercentHeight(30);
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

        gridLayout.getRowConstraints().addAll(rowTitle, rowType, rowVoice, rowChannel, rowSound);

        // Zorg dat de kolom de volledige breedte vult
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);
        gridLayout.getColumnConstraints().add(col);

        // Voeg de panes toe aan de juiste rij (node, columnIndex, rowIndex)
        gridLayout.add(filterTitlePane, 0, 0);
        gridLayout.add(filterTypePane, 0, 1);
        gridLayout.add(filterVoicePane, 0, 2);
        gridLayout.add(filterChannelPane, 0, 3);
        gridLayout.add(filterSoundPane, 0, 4);

        // Wis eventuele oude children en voeg de gridLayout toe aan deze VBox
        getChildren().clear();
        getChildren().add(gridLayout);

        logger.debug("Built {}", getId());
    }

     // ACCESSORS
    public EventListPane getEventListPane() {
        return parent;
    }
}
