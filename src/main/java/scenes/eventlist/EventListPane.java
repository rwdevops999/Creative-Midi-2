package scenes.eventlist;

import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.editor.EventListEditorPane;
import scenes.eventlist.filter.EventListFilterPane;

import static util.Util.setPaneBackground;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class EventListPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(EventListPane.class);

    public EventListPane() {
        super();

        setId("EventListPane");

        ColumnConstraints colEditor = new ColumnConstraints();
        colEditor.setPercentWidth(70);

        // 2. Define the 30% column for the filter
        ColumnConstraints colFilter = new ColumnConstraints();
        colFilter.setPercentWidth(30);

        // Add constraints to this (the parent, which now extends GridPane)
        this.getColumnConstraints().addAll(colEditor, colFilter);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100); // Forces the row to fill the parent height
        this.getRowConstraints().add(rowConstraints);

        setPaneBackground(this);

        buildPane();
    }

    private void buildPane() {
        logger.debug("Building {}", getId());

        add(new EventListEditorPane(this), 0, 0);
        add(new EventListFilterPane(this), 1, 0);

        logger.debug("Built {}", getId());
    }
}
