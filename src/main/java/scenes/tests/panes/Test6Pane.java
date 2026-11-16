package scenes.tests.panes;

import entity.voice.Group;
import entity.voice.Patch;
import fx.component.ImageButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import util.app.AppInfo;
import util.registry.Registry;
import util.voice.InstrumentProvider;
import util.voice.InstrumentProviderFactory;

import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
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

    private StringProperty dsResult = new SimpleStringProperty();
    private StringProperty dsText = new SimpleStringProperty();

    private static byte[] inBytes;
    private void setupTest() {
        byte[] in = {
                0x01,
                0x48, 0x65, 0x6C,
                0x6C, 0x6F, 0x00
        };

        inBytes = in;

        setSpacing(10);

        /* UI */
        Label inpLabel = new Label("Test6");
        getChildren().add(inpLabel);

        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(dsText);
        getChildren().add(textField);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            dsResult.set(makeClean(dsText.get()));
        });

        getChildren().add(submitButton);

        Label result = new Label();
        result.textProperty().bindBidirectional(dsResult);
        getChildren().add(result);
    }

    private String makeClean(String input) {
        StringBuilder sb = new StringBuilder();

        for (String token : input.trim().split("\\s+")) {
            if (token.isEmpty()) continue;

            int value = Integer.parseInt(token, 16);

            if (value == 0x00) {
                sb.append("NULL");
            } else if (value >= 0x20 && value <= 0x7E) {
                sb.append((char) value);
            }
        }

        String result = sb.toString();
        System.out.println("RESULT = " + result);

        return result;
    }

    private void runTest() {
    }
}
