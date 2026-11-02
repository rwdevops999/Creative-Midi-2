package scenes.eventlist.filter.subpanes;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChannelFilterPane extends VBox {
    public ChannelFilterPane() {
        super();

        setId("ChannelFilterPane");

        setMinSize(307.0, 153.0);
        setPrefSize(307.0, 153.0);
        setMaxSize(307.0, 153.0);

    }

    public ChannelFilterPane(Pane owner) {
        this();

        buildPane();
    }

    public void buildPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 0, 20));

        for (int i = 1; i <= 16; i++) {
            CheckBox checkBox = new CheckBox("Ch " + i);
            checkBox.setId("Ch" + i);

            // Finalize the bitmask for this checkbox
//            final Character currentBitMask = start;

//            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> consumer.accept(currentBitMask, newValue));

            // Check the checkboxes based on the input
//            checkBox.setSelected((initialConfig & start) != 0);

            // Add to the grid (column, row) -> creates a mice 4x4 matrix
            int col = (i - 1) % 4;
            int row = (i - 1) / 4;
            grid.add(checkBox, col, row);

            // Shift to next bit
//            start = (char) (start << 1);
        }

        getChildren().add(grid);
    }
}
