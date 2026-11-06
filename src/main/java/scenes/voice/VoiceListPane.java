package scenes.voice;

import entity.voice.Patch;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static util.app.colors.ColorScheme.getColor;

public class VoiceListPane extends AnchorPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceListPane.class);

    public VoiceListPane() {
        super();

        setId("VoiceListPane");
    }

    private final TableView<Patch> table = new TableView<>();

    private VoiceResultPane parent;
    public VoiceListPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
//        Util.showPaneBorder(this, getColor("border", "green"));

        setTopAnchor(table, 0.0);
        setBottomAnchor(table, 0.0);
        setLeftAnchor(table, 0.0);
        setRightAnchor(table, 0.0);

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        table.setId("VoicesTable");
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );
        createColumns(table);

        table.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();

            // Correct if necessary with 2 pixels for the outer edge of the table
        });

        getChildren().add(table);

        logger.debug("built {}", getId());
    }

    private TableColumn<Patch, String> nameColumn;

    private void createColumns(TableView<Patch> table) {
        nameColumn = new TableColumn<>();
        setColumnLabel(nameColumn, "Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);

        TableColumn<Patch, String> typeColumn = new TableColumn<>();
        setColumnLabel(typeColumn, "Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        table.getColumns().add(typeColumn);

        TableColumn<Patch, Integer> bankColumn  = new TableColumn<>();
        setColumnLabel(bankColumn, "Bank");
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bank"));
        table.getColumns().add(bankColumn);

        TableColumn<Patch, Integer> msbColumn  = new TableColumn<>();
        setColumnLabel(msbColumn, "MSB");
        msbColumn.setCellValueFactory(new PropertyValueFactory<>("msb"));
        table.getColumns().add(msbColumn);

        TableColumn<Patch, Integer> lsbColumn  = new TableColumn<>();
        setColumnLabel(lsbColumn, "LSB");
        lsbColumn.setCellValueFactory(new PropertyValueFactory<>("lsb"));
        table.getColumns().add(lsbColumn);

        TableColumn<Patch, Integer> pcColumn  = new TableColumn<>();
        setColumnLabel(pcColumn, "PC");
        pcColumn.setCellValueFactory(new PropertyValueFactory<>("pc"));
        table.getColumns().add(pcColumn);
    }

    private void setAbsoluteColumnWidth(TableColumn<?, ?> column, double width) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setPrefWidth(width);
    }

    private <T> void setColumnLabel(TableColumn<Patch, T> column, String label) {
        Label columnLabel = new Label(label);
        columnLabel.setMaxWidth(Double.MAX_VALUE); // Forces the label to span the whole column width
        columnLabel.setAlignment(Pos.CENTER_LEFT);  // Aligns text left
        column.setGraphic(columnLabel);
    }

    private List<Patch> currentData;
    public void showResults (List<Patch> data) {
        currentData = data;

        if (data != null) {
            data = data.stream().filter(patch -> ! excluded.contains(patch.getType())).toList();
        }

        ObservableList<Patch> tableData = FXCollections.observableArrayList(data);
        table.setItems(tableData);

        autoSizeColumn(table, nameColumn, 70);
    }

    private List<String> excluded = new ArrayList<>();

    public void setExcludedVoiceTypes(List<String> excludedVoiceTypes) {
        this.excluded =  excludedVoiceTypes;
        showResults(currentData);
    }

    private static final Map<TableColumn<?, ?>, Double> originalWidths =
            new WeakHashMap<>();

    public static <T> void autoSizeColumn(
            TableView<T> table,
            TableColumn<T, String> nameColumn,
            double widthPercent) {

        Platform.runLater(() -> {

            var columns = table.getVisibleLeafColumns();

            double totalWidth = table.getWidth() - 2;

            if (totalWidth <= 0) return;

            // Save original widths only once
            for (TableColumn<T, ?> col : columns) {
                if (col != nameColumn) {
                    originalWidths.putIfAbsent(col, col.getWidth());
                }
            }

            // Original width of all other columns
            double otherWidth = 0;

            for (TableColumn<T, ?> col : columns) {
                if (col != nameColumn) {
                    otherWidth += originalWidths.get(col);
                }
            }

            // Original available width for nameColumn
            double originalNameWidth = totalWidth - otherWidth;

            // Apply percentage to ORIGINAL available width
            double nameWidth = originalNameWidth *
                    Math.max(0, Math.min(100, widthPercent)) / 100.0;

            nameWidth = Math.max(
                    nameColumn.getMinWidth(), nameWidth
            );

            // Remaining width for other columns
            double remainingWidth = totalWidth - nameWidth;

            nameColumn.setPrefWidth(nameWidth);

            // Redistribute remaining space proportionally
            if (otherWidth > 0) {
                for (TableColumn<T, ?> col : columns) {
                    if (col == nameColumn) continue;

                    double originalWidth = originalWidths.get(col);

                    double newWidth =
                            remainingWidth * originalWidth / otherWidth;

                    col.setPrefWidth(newWidth);
                }
            }
        });
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
