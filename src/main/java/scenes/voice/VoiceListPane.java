package scenes.voice;

import entity.voice.Patch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import java.util.List;

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
//        Util.showSmallPaneBorder(this, getColor("border", "green"));
        Util.showPaneBorder(this, getColor("border", "green"));

        setTopAnchor(table, 0.0);
        setBottomAnchor(table, 0.0);
        setLeftAnchor(table, 0.0);
        setRightAnchor(table, 0.0);

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        table.setId("VoicesTable");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        createColumns(table);

        getChildren().add(table);

        logger.debug("built {}", getId());
    }

    private TableColumn<Patch, String> nameColumn;

    private void createColumns(TableView<Patch> table) {
        nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);

        TableColumn<Patch, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        table.getColumns().add(typeColumn);

        TableColumn<Patch, Integer> bankColumn  = new TableColumn<>("Bank");
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bank"));
        table.getColumns().add(bankColumn);

        TableColumn<Patch, Integer> msbColumn  = new TableColumn<>("MSB");
        msbColumn.setCellValueFactory(new PropertyValueFactory<>("msb"));
        table.getColumns().add(msbColumn);

        TableColumn<Patch, Integer> lsbColumn  = new TableColumn<>("LSB");
        lsbColumn.setCellValueFactory(new PropertyValueFactory<>("lsb"));
        table.getColumns().add(lsbColumn);

        TableColumn<Patch, Integer> pcColumn  = new TableColumn<>("PC");
        pcColumn.setCellValueFactory(new PropertyValueFactory<>("pc"));
        table.getColumns().add(pcColumn);
    }


    public void showResults (List<Patch> data) {
        System.out.println("Showing Results: " + data.size());
        ObservableList<Patch> tableData = FXCollections.observableArrayList(data);
        table.setItems(tableData);

        autoFitColumn(table, nameColumn);
    }

    private static <S, T> void autoFitColumn(TableView<S> tableView, TableColumn<S, T> column) {
        Platform.runLater(() -> {
            // Start with the width of the header text (plus some padding)
            Text headerText = new Text(column.getText());
            double maxWidth = headerText.getLayoutBounds().getWidth() + 20.0;

            // Iterate through rows to find the longest cell text
            for (S item : tableView.getItems()) {
                if (column.getCellValueFactory() != null) {
                    var observableValue = column.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, column, item));
                    if (observableValue != null && observableValue.getValue() != null) {
                        String cellTextString = observableValue.getValue().toString();
                        Text cellTextNode = new Text(cellTextString);
                        double cellWidth = cellTextNode.getLayoutBounds().getWidth() + 20.0; // 20px padding for cell borders

                        if (cellWidth > maxWidth) {
                            maxWidth = cellWidth;
                        }
                    }
                }
            }
            // Explicitly set the column width
            column.setPrefWidth(maxWidth);
        });
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
