package scenes.tests.panes;

import entity.voice.Group;
import entity.voice.Patch;
import fx.component.ImageButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import util.app.AppInfo;
import util.registry.Registry;
import util.voice.InstrumentProvider;
import util.voice.InstrumentProviderFactory;

import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static util.Util.setPaneBackground;

public class Test6Pane extends VBox {
    public Test6Pane() {
        super();

        setId("Test6Pane");

        setPaneBackground(this);

        Registry.getInstance().showStatus("Running Test6");

        setupTest();
        runTest();
    }

    private void setupTest() {
        ImageButton ib1 = new ImageButton("icons/upload-up.png", 100, 35);
        getChildren().add(ib1);

        ImageButton mb = new ImageButton("icons/merge.png", 100, 100);
        getChildren().add(mb);

        ImageButton ib2 = new ImageButton("icons/upload-down.png", 100, 35);
        getChildren().add(ib2);
    }

    private void runTest() {
    }
}
