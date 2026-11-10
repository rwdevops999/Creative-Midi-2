package fx.component;

import entity.NameEntity;
import fx.display.EntityListCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static util.Util.setPaneSizeAsPercentage;
import static util.app.colors.ColorScheme.getColor;

public class ComboValuePane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(ComboValuePane.class);

    public ComboValuePane() {
        super();

        setId("ComboValuePane");

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
        setAlignment(Pos.CENTER);
    }

    public ComboValuePane(Pane owner, String label, ObjectProperty<NameEntity> dsValue, BooleanProperty dsEnabled, List<NameEntity> comboValues, NameEntity selected, double width, double height, int colspan) {
        this();

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane(label,dsValue,dsEnabled,comboValues,selected,colspan);
    }

    private ComboBox<NameEntity> comboBox;

    private void buildPane(String label, ObjectProperty<NameEntity> dsValue, BooleanProperty dsEnabled, List<NameEntity> comboValues, NameEntity selected, int colspan) {
        logger.debug("Building {}", getId());

        if (label != null) {
            Label labelValue = new Label(label);
            add(labelValue, 0, 0, colspan-1, 1);
        }

        comboBox = new ComboBox<>();
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.disableProperty().bind(dsEnabled.not());
        comboBox.setCellFactory(listView -> new EntityListCell<>() );
        comboBox.getItems().addAll(comboValues);
            comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // create the cell and JavaFX links the 'newVal' to it.
                    comboBox.setButtonCell(new EntityListCell<>());
                }
            });
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    dsValue.setValue(newValue);
                }
            });
        if (selected != null) {
            comboBox.getSelectionModel().select(selected);
        }

        add(comboBox, colspan-1, 0, 4, 1);

        logger.debug("Built {}", getId());
    }

    public void updateComboValues(List<NameEntity> comboValues) {
        if (comboBox != null) {
            comboBox.getItems().clear();
            comboBox.getItems().addAll(comboValues);
        }
    }
}
