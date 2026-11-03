package scenes.eventlist.filter.subpanes;

import javafx.beans.property.IntegerProperty;
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

    private IntegerProperty dsLocalMask;
    public ChannelFilterPane(Pane owner, IntegerProperty dsMask) {
        this();

        dsLocalMask = dsMask;

        buildPane(dsMask);
    }

    private static void toggleBit(IntegerProperty dsMask, int bitPosition) {
        dsMask.set(dsMask.get() ^ (1 << bitPosition));
    }

    public void buildPane(IntegerProperty dsMask) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 0, 20));

        for (int i = 1; i <= 16; i++) {
            CheckBox checkBox = new CheckBox("Ch " + i);
            final int val = i-1;
            checkBox.setOnAction(event -> {
                toggleBit(dsMask, val);
            });
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

    public void selectAll() {
        for (int i = 1; i <= 16; i++) {
            CheckBox cb = (CheckBox)lookup("#Ch" + i);
            cb.setSelected(true);
        }

        dsLocalMask.set(0xFFFF);
    }

    public void selectNone() {
        for (int i = 1; i <= 16; i++) {
            CheckBox cb = (CheckBox)lookup("#Ch" + i);
            cb.setSelected(false);
        }

        dsLocalMask.set(0x0000);
    }

    public void selectVoices() {
        for (int i = 1; i <= 16; i++) {
            CheckBox cb = (CheckBox)lookup("#Ch" + i);
            cb.setSelected(i > 3);
            dsLocalMask.set(0xFFF8);
        }
    }

    public void selectRythms() {
        for (int i = 1; i <= 16; i++) {
            CheckBox cb = (CheckBox)lookup("#Ch" + i);
            cb.setSelected(i < 9);
            dsLocalMask.set(0x00FF);
        }
    }
}
