package scenes.eventlist.filter.subpanes;

import fx.component.LightIndicatorButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import static util.Util.showSmallPaneBorder;

public class MidiTypeFilterPane extends GridPane {
    public MidiTypeFilterPane() {
        super();

        setId("MidiTypeFilterPane");

        setMinSize(307.0, 143.0);
        setPrefSize(307.0, 143.0);
        setMaxSize(307.0, 143.0);

        // Optional: Voeg een beetje tussenruimte (gap) toe tussen de knoppen
        setHgap(10); // Horizontale ruimte tussen kolommen
        setVgap(10); // Verticale ruimte tussen rijen

        setPadding(new Insets(30,0,0,0));

        // 2. Definieer 3 kolommen die elk 33.33% innemen en ALLES centreren
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 3.0);
            col.setHalignment(HPos.CENTER); // Centreer horizontaal in de cel
            getColumnConstraints().add(col);
        }

        // 3. Definieer 2 rijen die elk 50% innemen en ALLES centreren
        for (int i = 0; i < 2; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(33.0);
            row.setValignment(VPos.CENTER); // Centreer verticaal in de cel
            getRowConstraints().add(row);
        }
    }

    public MidiTypeFilterPane(Pane owner) {
        this();

        buildPane();
    }

    private BooleanProperty dsSelected1 = new SimpleBooleanProperty(true);
    private BooleanProperty dsSelected2 = new SimpleBooleanProperty(true);
    private BooleanProperty dsSelected3 = new SimpleBooleanProperty(true);

    public void buildPane() {
        LightIndicatorButton lib1 = new LightIndicatorButton("MIDI", 50, "blue");
        lib1.selectedProperty().bindBidirectional(dsSelected1);
        add(lib1, 0, 0);
        LightIndicatorButton lib2 = new LightIndicatorButton("META", 50, "blue");
        lib2.selectedProperty().bindBidirectional(dsSelected2);
        add(lib2, 1, 0);
        LightIndicatorButton lib3 = new LightIndicatorButton("SYSEX", 50, "blue");
        lib3.selectedProperty().bindBidirectional(dsSelected3);
        add(lib3, 2, 0);

        Button det1 = new Button("Details");
        det1.setPrefWidth(60);
        add(det1, 0, 1);

        Button det2 = new Button("Details");
        det2.setPrefWidth(60);
        add(det2, 1, 1);

        Button det3 = new Button("Details");
        det3.setPrefWidth(60);
        add(det3, 2, 1);
    }
}
