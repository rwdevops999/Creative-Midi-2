package scenes.sysex;

import app.CreativeApp;
import entity.sysex.Sysex;
import fx.component.LabelValuePane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.statemachine.States;
import util.Util;
import util.app.AppInfo;

import static util.Util.*;
import static util.app.colors.ColorScheme.getColor;

public class SysexDetailsPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(SysexDetailsPane.class);

    public SysexDetailsPane() {
        super();

        setId("SysexDetailsPane");

        setPadding(new Insets(5));
    }

    private Sysex currentSysex;

    private SysexPane parent;
    public SysexDetailsPane(SysexPane owner, int width, int height) {
        this();

        parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);

        setPaneBackground(this);

        buildPane();
    }

    private StringProperty dsName = new SimpleStringProperty();
    private StringProperty dsSysex = new SimpleStringProperty();

    private void buildPane() {
        logger.debug("Building {}", getId());

        if (currentSysex != null) {
            getChildren().clear();

            LabelValuePane nameLabelValuePane = new LabelValuePane(this, "Name:", dsName, 90, 6, 4);
            VBox.setMargin(nameLabelValuePane, Insets.EMPTY);
//            nameLabelValuePane.setStyle("-fx-border-color: blue; -fx-border-width: 1;");
            getChildren().add(nameLabelValuePane);

            dsName.addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentSysex.setName(newValue);
                    checkChanges();
                }
            });

            LabelValuePane sysexLabelValuePane = new LabelValuePane(this, "Sysex:", dsSysex, 90, 6, 8);
            VBox.setMargin(sysexLabelValuePane, Insets.EMPTY);
            getChildren().add(sysexLabelValuePane);

            dsSysex.addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentSysex.getContent().setSysex(newValue);
                    checkChanges();
                }
            });

        }

        logger.debug("Built {}", getId());
    }

    public void setSysex(Sysex sysex) {
        AppInfo.INSTANCE.setSysex(new Sysex(sysex));

        this.currentSysex = sysex;

        dsName.setValue(sysex.getName());
        dsSysex.setValue(sysex.getContent().getSysex());

        buildPane();
    }

    private void checkChanges() {
        boolean originalEmpty = AppInfo.INSTANCE.getSysex().isEmpty();
        boolean currentEmpty = currentSysex.isEmpty();

        if (originalEmpty) {
            if (! currentEmpty) {
                getSysexPane().changeState(States.ADD);
            } else {
                getSysexPane().changeState(States.NEW);
            }
        } else {
            boolean equal = AppInfo.INSTANCE.getSysex().equals(currentSysex);
            if (!equal) {
                getSysexPane().changeState(States.UPDATE);
            } else {
                getSysexPane().changeState(States.SELECT);
            }
        }
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
