package scenes.voice.subpanes;

import fx.component.CopyButtonsPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import scenes.voice.VoiceSearchExclusionPane;
import util.voice.VoiceFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static util.Util.setPaneWidthAsPercentage;

public class ExcludePane extends GridPane {
    public ExcludePane() {
        super();

        setId("ExcludePane");

        int[] columnSizes = {25,10,25};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        setPadding(new Insets(0,0,0,5));
    }

    private ListView<String> availableTypesListView;
    private ListView<String> excludedTypesListView;

    private VoiceSearchExclusionPane parent;

    private List<String> availableVoiceTypes;
    private List<String> excludedVoiceTypes = new ArrayList<>();

    public ExcludePane(VoiceSearchExclusionPane owner) {
        this();

        parent = owner;

        availableVoiceTypes = VoiceFinder.getVoiceTypes();

        buildPanel();
    }

    private BooleanProperty dsDisabledAdd = new SimpleBooleanProperty(true);
    private BooleanProperty dsDisabledRemove = new SimpleBooleanProperty(false);

    private void buildPanel() {
        int row = 0;

        // ROW (available, excluded)
        Label available = new Label("Available");
        add(available, 0, row);

        Label excluded = new Label("Excluded");
        add(excluded, 2, row);

        row++;
        ObservableList<String> availableItems = FXCollections.observableArrayList(availableVoiceTypes);
        availableTypesListView = new ListView<>();
        availableTypesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableTypesListView.getStyleClass().add("no-horizontal-scroll");
        availableTypesListView.setItems(availableItems);
        add(availableTypesListView, 0, row, 1, 4);

        ObservableList<String> excludedItems = FXCollections.observableArrayList(excludedVoiceTypes);
        excludedTypesListView = new ListView<>();
        excludedTypesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        excludedTypesListView.getStyleClass().add("no-horizontal-scroll");
        excludedTypesListView.setItems(excludedItems);
        add(excludedTypesListView, 2, row, 1, 4);

        // Buttons
        CopyButtonsPane copyButtonsPane = new CopyButtonsPane(this);
        add(copyButtonsPane, 1, row, 1, 4);

        Button addAll = copyButtonsPane.getAddAllButton();
        addAll.disableProperty().bindBidirectional(dsDisabledAdd);

        Button addSel = copyButtonsPane.getAddSelectionButton();
        addSel.disableProperty().bindBidirectional(dsDisabledAdd);

        Button delSel = copyButtonsPane.getDeleteSelectionButton();
        delSel.disableProperty().bindBidirectional(dsDisabledRemove);

        Button delAll = copyButtonsPane.getDeleteAllButton();
        delAll.disableProperty().bindBidirectional(dsDisabledRemove);

        dsDisabledAdd.set(availableVoiceTypes.isEmpty());
        dsDisabledRemove.set(excludedVoiceTypes.isEmpty());

        addAll.setOnAction(e -> {
            if (! availableVoiceTypes.isEmpty()) {
                move(availableVoiceTypes, excludedVoiceTypes);
                updateListViews();
                updateResults();
            }
        });

        addSel.setOnAction(e -> {
            if (! availableTypesListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                excludedVoiceTypes = moveSelected (availableVoiceTypes, excludedVoiceTypes, new HashSet<>(availableTypesListView.getSelectionModel().getSelectedItems()));
                updateListViews();
                updateResults();
            }
        });

        delSel.setOnAction(e -> {
            if (! excludedTypesListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                availableVoiceTypes = moveSelected (excludedVoiceTypes, availableVoiceTypes, new HashSet<>(excludedTypesListView.getSelectionModel().getSelectedItems()));
                updateListViews();
                updateResults();
            }
        });

        delAll.setOnAction(e -> {
            if (! excludedVoiceTypes.isEmpty()) {
                move (excludedVoiceTypes, availableVoiceTypes);
                updateListViews();
                updateResults();
            }
        });
    }

    private void move (List<String> src, List<String> dst) {
        dst.addAll(src);
        src.clear();
    }

    private List<String> moveSelected (List<String> src, List<String> dst, Set<String> selected) {
        dst.addAll(selected);
        src.removeAll(selected);

        return dst;
    }

    private void updateListViews () {
        getChildren().clear();
        buildPanel();
    }

    private void updateResults() {
        getVoiceSearchExclusionPane().getVoiceSearchPane().getVoicePane().getVoiceResultPane().getVoiceListPane().setExcludedVoiceTypes(excludedVoiceTypes);
    }


    // ACCESSOR
    public VoiceSearchExclusionPane getVoiceSearchExclusionPane() {
        return parent;
    }
}
