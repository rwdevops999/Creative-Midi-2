package fx.dialog;

import entity.NameEntity;
import entity.playlist.Song;
import entity.sysex.Sysex;
import fx.component.Selector;
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

    public SysexMappingDialog(TableView<Sysex> table, List<Sysex> existingMappings, boolean isUpdate) {
        action = isUpdate ? "Update" : "Add";

        setTitle(action + " Mapping");
        setHeaderText("Define the " + table.getId() + " mappings");

        buildDialogContent(table, existingMappings, isUpdate);
//       buildDialogContent(isUpdate, currentMapping, existingsMappings);

        Optional<Sysex> result = showAndWait();

        result.ifPresent(rvalue -> {
            if (isUpdate) {
                int index = table.getItems().indexOf(table.getSelectionModel().getSelectedItem());
                if (index != -1) {
                    table.getItems().set(index, rvalue);
                    index = existingMappings.indexOf(rvalue);
                    existingMappings.set(index, rvalue);
                }
            } else if (table.getItems() != null) {
                Sysex sysex = new Sysex(rvalue);
                table.getItems().add(sysex);
                existingMappings.add(sysex);
            }
        });
    }

//    private void buildDialogContent(boolean isUpdate, List<Sysex> existingMappings) {
    private void buildDialogContent(TableView<Sysex> table, List<Sysex> existingMappings, boolean isUpdate) {
        ButtonType changeButtonType = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        Label typeLabel = new Label("Sysex");
        content.add(typeLabel, 0, 1);

        List<String> available = new ArrayList<>(SysexContainer.getInstance().getSysexNames());
        List<String> existing = existingMappings.stream().map(NameEntity::getName).toList();
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
