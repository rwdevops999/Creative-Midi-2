package fx.display;

import entity.NameEntity;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

/**
 * Can be used e.g. in combo box
 * - adjust the display of the dropdown-list
 * comboBox.setCellFactory(listView -> new EntityListCell());
 * or for a list view
 * listView.setCellFactory(listView -> new EntityListCell());
 * - adjust the display of the selected item in the combo box
 * comboBox.setButtonCell(new EntityListCell());
 */
public class EntityListCell extends ListCell<NameEntity> {
    @Override
    protected void updateItem(NameEntity item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            if (! item.getTextIcon().isEmpty()) {
                Text icon = new Text(item.getTextIcon());
                icon.setStyle("-fx-fill: #555555; -fx-font-weight: bold;");
                setGraphic(icon);
            }
        }
    }
}
