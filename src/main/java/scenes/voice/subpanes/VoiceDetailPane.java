package scenes.voice.subpanes;

import entity.voice.Patch;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class VoiceDetailPane extends GridPane {
    public VoiceDetailPane() {
        super();

        setId("VoiceDetailPane");

        int[] columnSizes = {1,10,1,10,5,10,1,10,5,10,1,10,5,10,1,10};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraint);
    }

    public VoiceDetailPane(Patch patch) {
        this();

        buildPane(patch);
    }

    private void buildPane(Patch patch) {
        // ROW 0 (BANK)
        Label bankLabel = new Label("Bank:");
        int row = 0;
        add(bankLabel, 1, row, 3, 1);

        Label bankValueLabel = new Label("" + patch.getBank());
        bankValueLabel.setStyle("-fx-font-weight: bold;");
        add(bankValueLabel, 4, row, 2, 1);

        // ROW 1 (MSB)
        row++;
        Label msbLabel = new Label("MSB:");
        add(msbLabel, 1, row, 3, 1);

        Label msbValueLabel = new Label("" + patch.getMsb());
        msbValueLabel.setStyle("-fx-font-weight: bold;");
        add(msbValueLabel, 4, row, 2, 1);

        // ROW 2 (LSB)
        row++;
        Label lsbLabel = new Label("LSB:");
        add(lsbLabel, 1, row, 3, 1);

        Label lsbValueLabel = new Label("" + patch.getLsb());
        lsbValueLabel.setStyle("-fx-font-weight: bold;");
        add(lsbValueLabel, 4, row, 2, 1);

        // ROW 3 (PC)
        row++;
        Label pcLabel = new Label("PC:");
        add(pcLabel, 1, row, 3, 1);

        Label pcValueLabel = new Label("" + patch.getPc());
        pcValueLabel.setStyle("-fx-font-weight: bold;");
        add(pcValueLabel, 4, row, 2, 1);

        // ROW 4 (SEPARATOR)
        row++;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        add(separator, 1, row, 7, 1);

        // ROW 5 (PATH)
        row++;
        Label pathLabel = new Label("Path:");
        add(pathLabel, 1, row, 3, 1);

        Label pathValueLabel = new Label(patch.getPath());
        pathValueLabel.setStyle("-fx-font-weight: bold;");
        add(pathValueLabel, 4, row, 8, 1);
    }
}
