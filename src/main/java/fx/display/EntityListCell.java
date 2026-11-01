package fx.display;

import entity.NameEntity;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Can be used e.g. in combo box
 * - adjust the display of the dropdown-list
 * comboBox.setCellFactory(listView -> new EntityListCell());
 * or for a list view
 * listView.setCellFactory(listView -> new EntityListCell());
 * - adjust the display of the selected item in the combo box
 * comboBox.setButtonCell(new EntityListCell());
 */
public class EntityListCell<T extends NameEntity> extends ListCell<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(null);
            if (! item.getTextIcon().isEmpty()) {
                Text icon = new Text(item.getTextIcon());
                icon.setStyle("-fx-fill: #555555; -fx-font-weight: bold;");
                setGraphic(icon);
            } else if (! item.getFontIcon().isEmpty()) {
                FontIcon icon = new FontIcon(item.getFontIcon());
                icon.setIconSize(16); // Size in pixels
                icon.setIconColor(javafx.scene.paint.Color.BLUE);
                setGraphic(icon);
            }
        }
    }
}
