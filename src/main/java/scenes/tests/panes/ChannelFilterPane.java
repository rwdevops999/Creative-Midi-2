package scenes.tests.panes;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import static util.app.colors.ColorScheme.getColor;

public class ChannelFilterPane extends GridPane {
    public ChannelFilterPane() {
        super();

        setId("ChannelFilterPane");

        setMinSize(307.0, 153.0);
        setPrefSize(307.0, 153.0);
        setMaxSize(307.0, 153.0);

        // Optional: Voeg een beetje tussenruimte (gap) toe tussen de knoppen
        setHgap(10); // Horizontale ruimte tussen kolommen
        setVgap(10); // Verticale ruimte tussen rijen

        // 2. Definieer 3 kolommen die elk 33.33% innemen en ALLES centreren
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 3.0);
            col.setHalignment(HPos.CENTER); // Centreer horizontaal in de cel
            getColumnConstraints().add(col);
        }

        // 3. Definieer 2 rijen die elk 50% innemen en ALLES centreren
        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(20.0);
            row.setValignment(VPos.CENTER); // Centreer verticaal in de cel
            getRowConstraints().add(row);
        }
    }

    public ChannelFilterPane(Pane owner) {
        this();

        buildPane();
    }

    public void buildPane() {
    }
}
