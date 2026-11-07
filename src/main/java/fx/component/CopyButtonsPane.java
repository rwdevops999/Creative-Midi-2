package fx.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import util.Util;

public class CopyButtonsPane extends VBox {
    public CopyButtonsPane() {
        super();

        setId("CopyButtonsPane");

        setSpacing(5);

        setMinSize(40, 140);
        setMaxSize(40, 140);
        setPrefSize(40, 140);

        setAlignment(Pos.CENTER);
    }

    public CopyButtonsPane(Pane owner) {
        this();

        buildPane();
    }

    private static final String flatButtonStyle =
            "-fx-background-color: #a9a9a9; " +          // Gray background (or 'transparent')
                    "-fx-background-radius: 0; " +       // Square corners
                    "-fx-text-fill: #333333; " +         // Text color
                    "-fx-font-size: 14px; " +            // Text size
                    "-fx-font-weight: bold; " +          // Bold text
                    "-fx-cursor: hand;";

    @Getter
    private Button addAllButton;
    @Getter
    private Button addSelectionButton;
    @Getter
    private Button deleteSelectionButton;
    @Getter
    private Button deleteAllButton;

    private void buildPane() {
        addAllButton = new Button("»"); // >>
//        addAll.setDisable(availableVoiceTypes.isEmpty());
        addAllButton.setAlignment(Pos.CENTER);
        addAllButton.setStyle(flatButtonStyle);
        addAllButton.setOnAction(e -> {
/*            if (! availableVoiceTypes.isEmpty()) {
                move(availableVoiceTypes, excludedVoiceTypes);
                updateListViews();
                updateResults();
            } */
        });
        getChildren().add(addAllButton);

        addSelectionButton = new Button(">"); // >
//        addSel.setDisable(availableVoiceTypes.isEmpty());
        addSelectionButton.setStyle(flatButtonStyle);
        addSelectionButton.setAlignment(Pos.CENTER);
        addSelectionButton.setOnAction(e -> {
/*            if (! availableTypesListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                excludedVoiceTypes = moveSelected (availableVoiceTypes, excludedVoiceTypes, new HashSet<>(availableTypesListView.getSelectionModel().getSelectedItems()));
                updateListViews();
                updateResults();
            } */
        });
        getChildren().add(addSelectionButton);

        deleteSelectionButton = new Button("<"); // <
//        delSel.setDisable(excludedVoiceTypes.isEmpty());
        deleteSelectionButton.setStyle(flatButtonStyle);
        deleteSelectionButton.setAlignment(Pos.CENTER);
        deleteSelectionButton.setOnAction(e -> {
/*            if (! excludedTypesListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                availableVoiceTypes = moveSelected (excludedVoiceTypes, availableVoiceTypes, new HashSet<>(excludedTypesListView.getSelectionModel().getSelectedItems()));
                updateListViews();
                updateResults();
            } */
        });
        getChildren().add(deleteSelectionButton);

        deleteAllButton = new Button("«"); // <<
//        delAll.setDisable(excludedVoiceTypes.isEmpty());
        deleteAllButton.setAlignment(Pos.CENTER);
        deleteAllButton.setStyle(flatButtonStyle);
        deleteAllButton.setOnAction(e -> {
/*            if (! excludedVoiceTypes.isEmpty()) {
                move (excludedVoiceTypes, availableVoiceTypes);
                updateListViews();
                updateResults();
            } */
        });
        getChildren().add(deleteAllButton);
    }
}
