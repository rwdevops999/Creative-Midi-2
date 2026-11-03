package fx.dialog;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

import java.util.ArrayList;
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

        buildContent(buttonLabel);
    }

    private void buildContent(String buttonLabel) {
        ButtonType useButtonType = new ButtonType(buttonLabel, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(useButtonType, ButtonType.CANCEL);

        getDialogPane().setContent(buildContentPane());

        setResultConverter(dialogButton -> {
            if (dialogButton == useButtonType) {
                return new ArrayList<T>();
            }

            return null; // return null when we cancel
        });
    }

    private HBox buildContentPane() {
        HBox hBox = new HBox();

        Label testLabel = new Label("Test");
        hBox.getChildren().add(testLabel);

        return hBox;
    }
}
