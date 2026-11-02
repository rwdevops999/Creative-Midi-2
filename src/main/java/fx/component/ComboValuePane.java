package fx.component;

import entity.NameEntity;
import fx.display.EntityListCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static util.Util.setPaneSizeAsPercentage;

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
    }

    public ComboValuePane(Pane owner, String label, StringProperty dsProp, BooleanProperty dsEnabled, int width, int height, int cols, List<NameEntity> comboValues) {
        this();

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane(label, dsProp, dsEnabled, cols, comboValues);
    }

    private void buildPane(String label, StringProperty dsProp, BooleanProperty dsEnabled, int cols, List<NameEntity> comboValues) {
        logger.debug("Building {} for {}", getId(), label);

        ComboBox<NameEntity> paraFunctionsCombobox = new ComboBox<>();
        paraFunctionsCombobox.disableProperty().bind(dsEnabled.not());
        paraFunctionsCombobox.setCellFactory(listView -> new EntityListCell<>() );
        paraFunctionsCombobox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // create the cell and JavaFX links the 'newVal' to it.
                paraFunctionsCombobox.setButtonCell(new EntityListCell<>());
            }
        });

        paraFunctionsCombobox.getItems().addAll(comboValues);
        paraFunctionsCombobox.getSelectionModel().select(comboValues.stream().filter(f -> f.getName().equalsIgnoreCase(dsProp.get())).findFirst().orElse(null));
        paraFunctionsCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               dsProp.setValue(newValue.getName());
           }
        });
        add(paraFunctionsCombobox, 1, 0, cols, 1);

        logger.debug("Built {} for {}", getId(), label);
    }
}
