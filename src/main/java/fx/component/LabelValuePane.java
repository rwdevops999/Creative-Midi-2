package fx.component;

import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.SysexDetailsPane;

import static util.Util.setPaneSizeAsPercentage;

public class LabelValuePane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(LabelValuePane.class);

    public LabelValuePane() {
        super();

        setId("LabelValuePane");

        // 1. Kolommen instellen (onveranderd)
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

    public LabelValuePane(Pane owner, String label, StringProperty dsProp, int width, int height, int cols) {
        this();

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane(label, dsProp, cols);
    }

    private void buildPane(String label, StringProperty dsProp, int cols) {
        logger.debug("Building {} for {}", getId(), label);
        Label staticLabel = new Label(label);
        add(staticLabel, 0, 0);

        TextField textField = new TextField();
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setPromptText("Name...");
        textField.textProperty().bindBidirectional(dsProp);
        add(textField, 1, 0, cols, 1);

        logger.debug("Built {} for {}", getId(), label);
    }
}
