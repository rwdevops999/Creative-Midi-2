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
                System.out.println("DSNAME CHANGED: " + newValue);
            });

            LabelValuePane sysexLabelValuePane = new LabelValuePane(this, "Sysex:", dsSysex, 90, 6, 8);
            VBox.setMargin(sysexLabelValuePane, Insets.EMPTY);
            getChildren().add(sysexLabelValuePane);

            dsSysex.addListener((observable, oldValue, newValue) -> {
                System.out.println("DSSYSEX CHANGED: " + newValue);
            });

        }

        logger.debug("Built {}", getId());
    }

    public void setSysex(Sysex sysex) {
        this.currentSysex = sysex;

        dsName.setValue(sysex.getName());
        dsSysex.setValue(sysex.getContent().getSysex());

        AppInfo.INSTANCE.setSysex(sysex);

        buildPane();
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
