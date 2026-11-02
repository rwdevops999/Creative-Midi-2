package fx.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Util.setPaneSizeAsPercentage;

public class CheckedValuePane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(CheckedValuePane.class);

    public CheckedValuePane() {
        super();

        setId("CheckedValuePane");

        int totalColumns = 10;
        double percentagePerColumn = (100.0 - 10.0) / totalColumns;
        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentagePerColumn);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        setPadding(new Insets(1, 0, 0, 0));
    }

    public CheckedValuePane(Pane owner, String label, BooleanProperty dsProp, int width, int height, int cols) {
        this();

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane(label, dsProp, cols);
    }

    private void buildPane(String label, BooleanProperty dsProp, int cols) {
        logger.debug("Building {} for {}", getId(), label);

        CheckBox checkBox = new CheckBox(label);
        checkBox.selectedProperty().bindBidirectional(dsProp);
        add(checkBox, 1, 0, cols, 1);

        logger.debug("Built {} for {}", getId(), label);
    }
}
