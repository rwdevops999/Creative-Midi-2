package fx.component;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Util.setPaneWidthAsPercentage;
import static util.app.colors.ColorScheme.getColor;

@Getter
public class TitledSearchPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(TitledSearchPane.class);

    public TitledSearchPane() {
        super();

        setId("TitledSearchPane");
    }

    public TitledSearchPane(Pane owner, String title) {
        this();

        Util.showSmallPaneBorder(this, getColor("border", "turquoise"));
        setPaneWidthAsPercentage(this, owner, 100);

        int[] columnSizes = {12,3,50,3,12,20};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        buildPane(owner, title);
    }

    private Button searchButton;

    private void buildPane(Pane owner, String title) {
        logger.debug("Building {}", getId());

        int row = 0;
        // ROW (label and search button)
        Label titleLabel = new Label(title);
        add(titleLabel, 0, row, 3, 1);

        searchButton = new Button("Search");
        searchButton.setOnAction(event -> {
/*            if (owner instanceof PatchSearchPane patchSearchPane) {
                patchSearchPane.handleSearch(false);
            } else if (owner instanceof GroupSearchPane groupSearchPane) {
                groupSearchPane.handleSearch();
            } */
        });

        add(searchButton, 4, row, 2, 1);

        logger.debug("Built {}", getId());
    }

}
