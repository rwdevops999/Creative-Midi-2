package scenes.sysex;

import entity.NameEntity;
import entity.sysex.Sysex;
import fx.component.CheckedValuePane;
import fx.component.ComboValuePane;
import fx.component.LabelValuePane;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.sysex.parametrized.ParametrizedFunctions;
import scenes.sysex.statemachine.States;
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
    private BooleanProperty dsParametrized = new SimpleBooleanProperty();
    private ObjectProperty<NameEntity> dsFunction = new SimpleObjectProperty<>();

    private void buildPane() {
        logger.debug("Building {}", getId());

        if (currentSysex != null) {
            getChildren().clear();

            LabelValuePane nameLabelValuePane = new LabelValuePane(this, "Name:", dsName, 90, 6, 4);
            getChildren().add(nameLabelValuePane);

            dsName.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && currentSysex != null) {
                    currentSysex.setName(newValue);
                    checkChanges();
                }
            });

            LabelValuePane sysexLabelValuePane = new LabelValuePane(this, "Sysex:", dsSysex, 90, 6, 8);
            getChildren().add(sysexLabelValuePane);

            dsSysex.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && currentSysex != null) {
                    currentSysex.getContent().setSysex(newValue);
                    checkChanges();
                }
            });

            CheckedValuePane parametrizedCheckedValuePane = new CheckedValuePane(this, "Parametrized", dsParametrized, 90, 6, 4);
            getChildren().add(parametrizedCheckedValuePane);
            dsParametrized.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && currentSysex != null) {
                    currentSysex.getContent().setParametrized(newValue);
                    if (! newValue) {
                        currentSysex.getContent().setParametrizedFunction(null);
                    } else {
                        currentSysex.getContent().setParametrizedFunction(dsFunction.get().getName());
                    }
                    checkChanges();
                }
            });

            ComboValuePane parametrizedSelectValuePane = new ComboValuePane(this, "Function", dsFunction, dsParametrized, ParametrizedFunctions.getParaFunctions(), ParametrizedFunctions.getParaFunctions().get(0), 90, 6, 3);
            getChildren().add(parametrizedSelectValuePane);
            dsFunction.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && currentSysex != null) {
                    currentSysex.getContent().setParametrizedFunction(newValue.getName());
                    checkChanges();
                }
            });

        } else {
            getChildren().clear();
        }

        logger.debug("Built {}", getId());
    }

    public void setSysex(Sysex sysex) {
        if (sysex != null) {
            AppInfo.INSTANCE.setSysex(new Sysex(sysex));
            dsName.setValue(sysex.getName());
            dsSysex.setValue(sysex.getContent().getSysex());
            dsParametrized.setValue(sysex.getContent().getParametrized());
            dsFunction.setValue(ParametrizedFunctions.getParaFunctions().stream().filter(pf -> pf.getName().equals(sysex.getContent().getParametrizedFunction())).findFirst().orElse(null));
            this.currentSysex = new Sysex(sysex);
        } else {
            AppInfo.INSTANCE.setSysex(null);
            dsName.setValue("");
            dsSysex.setValue("");
            dsParametrized.setValue(false);
            dsFunction.setValue(null);
            this.currentSysex = null;
        }

        buildPane();
    }

    private void checkChanges() {
        if (AppInfo.INSTANCE.getSysex() != null) {
            boolean originalEmpty = AppInfo.INSTANCE.getSysex().isEmpty();
            boolean currentEmpty = currentSysex.isEmpty();

            if (originalEmpty) {
                if (! currentEmpty) {
                    if (! currentSysex.getName().isEmpty()) {
                        getSysexPane().changeState(States.ADD);
                    }
                } else {
                    getSysexPane().changeState(States.NEW);
                }
            } else {
                boolean equal = AppInfo.INSTANCE.getSysex().equals(currentSysex);
                if (!equal) {
                    if (!currentSysex.getName().isEmpty()) {
                        getSysexPane().changeState(States.UPDATE);
                    }
                } else {
                    getSysexPane().changeState(States.SELECT);
                }
            }
        }
    }

    public Sysex getSysex() {
        return currentSysex;
    }

    // ACCESSORS
    public SysexPane getSysexPane() {
        return parent;
    }
}
