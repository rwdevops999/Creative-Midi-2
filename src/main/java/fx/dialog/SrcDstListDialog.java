package fx.dialog;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SrcDstListDialog<T extends String> extends Dialog<List<T>> {
    private final ListProperty<T> srcProperty = new SimpleListProperty<>();
    private final ListProperty<T> dstProperty = new SimpleListProperty<>();

    public SrcDstListDialog(String title, String headerText, String buttonLabel, String srcLabel, String dstLabel, List<T> srcList, List<T> dstList) {
        super();

        initStyle(StageStyle.UNDECORATED);

        srcProperty.set(FXCollections.observableArrayList(srcList));
        dstProperty.set(FXCollections.observableArrayList(dstList));

        setTitle(title);
        setHeaderText(headerText);

        updateDisables();

        buildContent(buttonLabel, srcLabel, dstLabel);
    }

    private void buildContent(String buttonLabel, String srcLabel, String dstLabel) {
        ButtonType useButtonType = new ButtonType(buttonLabel, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(useButtonType, ButtonType.CANCEL);

        getDialogPane().setContent(buildContentPane(srcLabel, dstLabel));

        setResultConverter(dialogButton -> {
            if (dialogButton == useButtonType) {
                return dstProperty.get();
            }

            return null; // return null when we cancel
        });
    }

    private ListView<T> srcListView;
    private ListView<T> dstListView;

    private HBox buildContentPane(String srcLabel, String dstLabel) {
        HBox hBox = new HBox();

        Pane pane = buildListPane(srcLabel, srcProperty);
        hBox.getChildren().add(pane);
        srcListView = lookup(pane);

        hBox.getChildren().add(buildButtonsPane());

        pane = buildListPane(dstLabel, dstProperty);
        hBox.getChildren().add(pane);
        dstListView = lookup(pane);

        return hBox;
    }

    private VBox buildListPane(String label, ListProperty<T> listProperty) {
        VBox vBox = new VBox();

        // setup VBOX
        vBox.setPadding(new Insets(10));

        // add components
        Label lbl = new Label(label);
        vBox.getChildren().add(lbl);

        ListView<T> listView = new ListView<>();
        listView.setId("listview");
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.itemsProperty().bind(listProperty);
        listView.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    String value = item.toString();
                    setText(value);
                }
            }
        });

        vBox.getChildren().add(listView);

        return vBox;
    }

    private final BooleanProperty addDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty removeDisabled = new SimpleBooleanProperty(true);

    private VBox buildButtonsPane() {
        VBox vBox = new VBox();

        // setup buttons pane
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        String flatButtonStyle =
                "-fx-background-color: #a9a9a9; " +     // Gray background (or 'transparent')
                        "-fx-background-radius: 0; " +  // Square hooks
                        "-fx-text-fill: #333333; " +    // Text color
                        "-fx-font-size: 14px; " +       // Size of characters
                        "-fx-font-weight: bold; " +     // Bold text
                        "-fx-cursor: hand;";

        Button addSelected = new Button(">");
        addSelected.disableProperty().bind(addDisabled);
        addSelected.setStyle(flatButtonStyle);
        addSelected.setOnAction(e -> {
            if (! srcListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                moveSelected (srcProperty, dstProperty, new ArrayList<>(srcListView.getSelectionModel().getSelectedItems()));
            }
        });
        vBox.getChildren().add(addSelected);

        Button addAll = new Button("»"); // >>
        addAll.disableProperty().bind(addDisabled);
        addAll.setStyle(flatButtonStyle);
        addAll.setOnAction(e -> moveSelected (srcProperty, dstProperty, srcProperty.get()));
        vBox.getChildren().add(addAll);

        Button removeAll = new Button("«"); // <<
        removeAll.disableProperty().bind(removeDisabled);
        removeAll.setStyle(flatButtonStyle);
        removeAll.setOnAction(e -> moveSelected (dstProperty, srcProperty, dstProperty.get()));
        vBox.getChildren().add(removeAll);

        Button removeSelected = new Button("<"); // <
        removeSelected.disableProperty().bind(removeDisabled);
        removeSelected.setStyle(flatButtonStyle);
        removeSelected.setOnAction(e -> {
            if (! dstListView.getSelectionModel().getSelectedIndices().isEmpty()) {
                moveSelected (dstProperty, srcProperty, new ArrayList<>(dstListView.getSelectionModel().getSelectedItems()));
            }
        });
        vBox.getChildren().add(removeSelected);

        return vBox;
    }

    private ListView<T> lookup(Pane pane) {
        return pane.getChildren().stream()
                .filter(node -> "listview".equals(node.getId()))
                .map(node -> (ListView<T>) node)
                .findFirst()
                .orElse(null);
    }

    private void sortList(ListProperty<T> list) {
        SortedList<T> sortedOnName = new SortedList<>(list.get(), Comparator.comparing(T::toString));
        list.set(FXCollections.observableArrayList(sortedOnName));
    }

    private void updateDisables() {
        addDisabled.set(srcProperty.get().isEmpty());
        removeDisabled.set(dstProperty.get().isEmpty());
    }

    private void moveSelected (ListProperty<T> from, ListProperty<T> to, List<T> list) {
        to.get().addAll(list);
        from.get().removeAll(list);

        sortList(to);

        updateDisables();
    }


}