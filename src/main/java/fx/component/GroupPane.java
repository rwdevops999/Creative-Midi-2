package fx.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import scenes.voice.VoiceSearchSelectionPane;
import util.app.AppInfo;
import util.registry.AppEvent;
import util.registry.Registry;
import util.voice.VoiceFinder;

import java.util.List;

import static util.Util.setPaneWidthAsPercentage;

public class GroupPane extends GridPane {

    @Getter
    private String selectedGroup = null;

    public GroupPane() {
        super();

        setId("GroupPane");

        int[] columnSizes = {12,3,50,3,12,20};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);
    }

    private VoiceSearchSelectionPane parent;

    public GroupPane(VoiceSearchSelectionPane owner) {
        this();

        parent = owner;

        setPaneWidthAsPercentage(this, owner, 100);

        buildPane();
    }

    private ComboBox<String> groupsComboBox;
    private void buildPane() {
        int row = 0;

        // ROW (+, input, -)
        List<String> groupNames = VoiceFinder.getGroupsNames(AppInfo.INSTANCE.getInstrumentProvider().getGroups(), "");
        ObservableList<String> groups = FXCollections.observableArrayList(groupNames);
        groupsComboBox = new ComboBox<>(groups);
        groupsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedGroup = newValue;
            linkedSearchButton.fire();
        });
        groupsComboBox.setMaxWidth(Double.MAX_VALUE);
        setShowingProperty(groupsComboBox);

        Registry.getInstance().register(event -> {
            if (event != null && AppEvent.AppEventType.VOICE.equals(event.type())) {
                List<String> newGroupNames = VoiceFinder.getGroupsNames(AppInfo.INSTANCE.getInstrumentProvider().getGroups(), "");

                groupsComboBox.getItems().clear();
                ObservableList<String> newGroups = FXCollections.observableArrayList(newGroupNames);
                groupsComboBox.setItems(newGroups);
                setShowingProperty(groupsComboBox);

                linkedSearchButton.fire();
            }
        });

/*        Registry.register("VoiceGroupSelector", groupsComboBox, (node, data) -> {
            List<String> newGroupNames = VoiceFinder.getGroupsNames(ApplicationInfo.getInstance().getInstrumentProvider().getGroups(), "");
            groupsComboBox.getItems().clear();
            ObservableList<String> newGroups = FXCollections.observableArrayList(newGroupNames);
            groupsComboBox.setItems(newGroups);
            setShowingProperty(groupsComboBox);

            // update excludes pane
            getGroupSearchPane().getVoiceSearchPane().getExcludesPane().updateExcludes();

            // clean results pane
            getGroupSearchPane().getVoiceSearchPane().getVoicePane().getVoiceSearchResultsPane().clear();
        });
*/
        groupsComboBox.setMaxWidth(Double.MAX_VALUE);
        add(groupsComboBox, 0, row, 5, 1);
    }

    public void setShowingProperty(ComboBox<String> comboBox) {
        comboBox.setOnShowing(event -> {
            if (comboBox.getSkin() instanceof ComboBoxListViewSkin<?> skin) {
                ListView<?> listView = (ListView<?>) skin.getPopupContent();
                if (listView != null) {
                    listView.scrollTo(0);

                    // Remove the handler so that this doesn't happen again the next time
                    comboBox.setOnShowing(null);
                }
            }
        });
    }

    private Button linkedSearchButton;
    public void addLinkedSearchButton(Button button) {
        linkedSearchButton = button;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
        groupsComboBox.getSelectionModel().select(null);
    }

    // ACCESSOR
    public VoiceSearchSelectionPane getVoiceSearchSelectionPane() {
        return parent;
    }
}
