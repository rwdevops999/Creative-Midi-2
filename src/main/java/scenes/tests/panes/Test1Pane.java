package scenes.tests.panes;

import app.root.root.header.panes.AppButtons;
import fx.component.LabeledSelector;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import static util.Constants.APP_HEIGHT;
import static util.Constants.APP_WIDTH;
import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class Test1Pane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(Test1Pane.class);

    public Test1Pane() {
        super();

        setId("Test1Pane");

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        setMaxWidth(APP_WIDTH * .10);
        setMinWidth(APP_WIDTH * .10);
        setPrefWidth(APP_WIDTH * .10);

        setMaxHeight(APP_HEIGHT * .15);
        setMinHeight(APP_HEIGHT * .15);
        setPrefHeight(APP_HEIGHT * .15);

        setPaneBackground(this);

        Util.showPaneBorder(this, getColor("border", "red"));

        buildPane();
    }

    private void buildPane() {
        AppButtons appButtons = new AppButtons(this);
        VBox.setVgrow(appButtons, javafx.scene.layout.Priority.ALWAYS);
        getChildren().add(appButtons);
    }

    private void runTest() {

    }
}
