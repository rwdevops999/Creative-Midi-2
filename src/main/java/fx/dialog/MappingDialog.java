package fx.dialog;

import entity.playlist.SongMapping;
import entity.sysex.Sysex;
import fx.component.Selector;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import scenes.sysex.container.SysexContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MappingDialog extends Dialog<SongMapping> {
    public MappingDialog(TableView<SongMapping> table, ListProperty<SongMapping> mappingProperty, boolean isUpdate) {
        setTitle(isUpdate ? "Update" : "Add" + " Mapping");
        setHeaderText("Define the song mappings");

        buildDialogContent(table, mappingProperty, isUpdate);

        Optional<SongMapping> result = showAndWait();

        result.ifPresent(rvalue -> {
            if (isUpdate) {
                int index = table.getItems().indexOf(table.getSelectionModel().getSelectedItem());
                if (index != -1) {
                    table.getItems().set(index, rvalue);
                    mappingProperty.set(FXCollections.observableArrayList(table.getItems()));
                }
            } else if (table.getItems() != null) {
                SongMapping mapping = new SongMapping(rvalue.getReceive(), rvalue.getReply());
                table.getItems().add(mapping);
                mappingProperty.set(FXCollections.observableArrayList(table.getItems()));
            }
        });
    }

    private void buildDialogContent(TableView<SongMapping> table, ListProperty<SongMapping> mappingProperty, boolean isUpdate) {
        ButtonType changeButtonType = new ButtonType(isUpdate ? "Update" : "Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        Label receiveLabel = new Label("Receive");
        content.add(receiveLabel, 0, 1);

        List<String> availableMappings = SysexContainer.getInstance().getSysexNames();

        List<String> receiveMappings = mappingProperty.get().stream().map(sm -> sm.getReceive().getName()).toList();
        List<String> availableReceiveMappings = new ArrayList<>(availableMappings);
        availableReceiveMappings.removeAll(receiveMappings);

        Selector<String> receiveSelect = new Selector<>(null, availableReceiveMappings, isUpdate ? table.getSelectionModel().getSelectedItem().getReceive().getName() : null);
        content.add(receiveSelect, 1, 1);

        List<String> replyMappings = mappingProperty.get().stream().map(sm -> sm.getReply().getName()).toList();
        List<String> availableReplyMappings = new ArrayList<>(availableMappings);
//        availableReplyMappings.removeAll(replyMappings);

        Label replyLabel = new Label("Reply");
        content.add(replyLabel, 0, 2);

        Selector<String> replySelect = new Selector<>(null, availableReplyMappings, isUpdate ? table.getSelectionModel().getSelectedItem().getReply().getName() : null);
        content.add(replySelect, 1, 2);

        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                if (isUpdate) {
                    SongMapping currentMapping = table.getSelectionModel().getSelectedItem();

                    currentMapping.setReceive(SysexContainer.getInstance().getSysexByName(receiveSelect.getSelectedItem()));
                    currentMapping.setReply(SysexContainer.getInstance().getSysexByName(replySelect.getSelectedItem()));

                    return currentMapping;
                }

                return new SongMapping(SysexContainer.getInstance().getSysexByName(receiveSelect.getSelectedItem()), SysexContainer.getInstance().getSysexByName(replySelect.getSelectedItem()));
            }
            return null;
        });

    }

}
