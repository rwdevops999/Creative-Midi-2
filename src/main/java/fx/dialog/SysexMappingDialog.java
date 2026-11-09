package fx.dialog;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.component.Selector;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import scenes.sysex.container.SysexContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SysexMappingDialog extends Dialog<Sysex> {
    private String action;

    public SysexMappingDialog() {
        super();
    }

    public SysexMappingDialog(TableView<Sysex> table, ListProperty<Sysex> mappingProperty, boolean isUpdate) {
        action = isUpdate ? "Update" : "Add";

        setTitle(action + " Mapping");
        setHeaderText("Define the " + table.getId() + " mappings");

        buildDialogContent(table, mappingProperty, isUpdate);
//       buildDialogContent(isUpdate, currentMapping, existingsMappings);

        Optional<Sysex> result = showAndWait();

        result.ifPresent(rvalue -> {
            if (isUpdate) {
                int index = table.getItems().indexOf(table.getSelectionModel().getSelectedItem());
                if (index != -1) {
                    table.getItems().set(index, rvalue);
                    mappingProperty.set(FXCollections.observableArrayList(table.getItems()));
                }
            } else if (table.getItems() != null) {
                Sysex sysex = new Sysex(rvalue);
                table.getItems().add(sysex);
                mappingProperty.set(FXCollections.observableArrayList(table.getItems()));
            }
        });
    }

//    private void buildDialogContent(boolean isUpdate, List<Sysex> existingMappings) {
    private void buildDialogContent(TableView<Sysex> table, ListProperty<Sysex>  mappingProperty, boolean isUpdate) {
        ButtonType changeButtonType = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        Label typeLabel = new Label("Sysex");
        content.add(typeLabel, 0, 1);

        List<String> available = new ArrayList<>(SysexContainer.getInstance().getSysexNames());
        List<String> existing = mappingProperty.get().stream().map(NameEntity::getName).toList();
        available.removeAll(existing);

        Selector<String> sysexSelect = new Selector<>(null, available, isUpdate ? table.getSelectionModel().getSelectedItem().getName() : null);
        content.add(sysexSelect, 1, 1);

        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return SysexContainer.getInstance().getSysexByName(sysexSelect.getSelectedItem());
            }

            return null;
        });
    }
}
