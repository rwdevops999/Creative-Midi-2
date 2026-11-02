package scenes.sysex;

import entity.sysex.Sysex;
import fx.display.EntityListCell;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.container.SysexContainer;
import scenes.sysex.statemachine.States;
import util.Util;

import java.util.ArrayList;
import java.util.List;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class SysexListPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SysexListPane.class);

    public SysexListPane() {
        super();

        setId("SysexListPane");
    }

    private SysexPane parent;
    public SysexListPane(SysexPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "red"));

        setPaneBackground(this);

        buildPane();
        setListviewItems();
    }

    private ListView<Sysex> sysexListView;
    private void buildPane() {
        logger.debug("Building {}", getId());

        sysexListView = new ListView<>();
        sysexListView.setCellFactory(listView -> new EntityListCell<>());
        VBox.setVgrow(sysexListView, Priority.ALWAYS);
        getChildren().add(sysexListView);

        sysexListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getSysexPane().changeState(States.SELECT);
            getSysexPane().getSysexDetailsPane().setSysex(newValue);
        });

        logger.debug("Built {}", getId());
    }

    private void setListviewItems() {
        sysexListView.getItems().clear();
        sysexListView.setItems(FXCollections.observableArrayList(SysexContainer.getInstance().getSysexList()));
    }

    public void reload(Sysex sysexToSelect) {
        setListviewItems();
        if (sysexToSelect != null) {
            sysexListView.getSelectionModel().select(sysexToSelect);
        } else {
            sysexListView.getSelectionModel().clearSelection();
            getSysexPane().changeState(States.IDLE);
        }
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
