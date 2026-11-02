package scenes.eventlist.filter.subpanes;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChannelControlPane extends VBox {
    public ChannelControlPane() {
        super();

        setId("ChannelFilterPane");

        setMinSize(307.0, 102.0);
        setPrefSize(307.0, 102.0);
        setMaxSize(307.0, 102.0);
    }

    public ChannelControlPane(Pane owner) {
        this();

        buildPane();
    }

    public void buildPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
//        grid.setPadding(new Insets(0, 0, 0, 20));

        Button b1 = new Button("All");
        b1.setPrefWidth(60);
        grid.add(b1, 0, 0);

        Button b2 = new Button("None");
        b2.setPrefWidth(60);
        grid.add(b2, 1, 0);

        Button b3 = new Button("Voices");
        b3.setPrefWidth(60);
        grid.add(b3, 0, 1);

        Button b4 = new Button("Rythm");
        b4.setPrefWidth(60);
        grid.add(b4, 1, 1);

        getChildren().add(grid);
    }
}
