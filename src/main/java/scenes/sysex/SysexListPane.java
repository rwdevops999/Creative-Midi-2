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
        // TestData
        List<Sysex> sysexList = new ArrayList<>();

        Sysex s1 = new Sysex("Test1");
        s1.getContent().setSysex("F0 01 02 03 F7");
        sysexList.add(s1);

        Sysex s2 = new Sysex("Test2");
        s2.getContent().setSysex("F0 0A 0B 11 C3 F7");
        sysexList.add(s2);

        sysexListView.getItems().clear();
//        sysexListView.setItems(FXCollections.observableArrayList(SysexContainer.getSysexList()));
        sysexListView.setItems(FXCollections.observableArrayList(sysexList));
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
