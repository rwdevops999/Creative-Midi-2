package scenes.tests.panes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.Util.setPaneBackground;

public class Test5Pane extends VBox {
    public Test5Pane() {
        super();

        setId("Test5Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private ObjectProperty<Color> dsColor = new SimpleObjectProperty<>(Color.WHITE);

    private  void  setupTest() {
        ObservableList<String> colorNames = FXCollections.observableArrayList(getColorNames());

        ComboBox<String> colorComboBox = new ComboBox<>(colorNames);
        colorComboBox.setPromptText("Select a color");

        colorComboBox.setOnAction(e -> {
            String selectedName = colorComboBox.getValue();
            if (selectedName != null) {
                Color color = Color.valueOf(selectedName);
                dsColor.set(color);
            }
        });

        getChildren().add(colorComboBox);

        ColorRectangle cr1 = new ColorRectangle(Color.WHITE, dsColor);
        getChildren().add(cr1);

        ColorRectangle cr2 = new ColorRectangle(Color.DARKGRAY, dsColor);
        getChildren().add(cr2);
    }

    private void runTest() {
    }

    private List<String> getColorNames() {
        List<String> names = new ArrayList<>();
        Field[] fields = Color.class.getDeclaredFields();

        for (Field field : fields) {
            // Filter op velden die public, static én van het type Color zijn
            if (Modifier.isPublic(field.getModifiers()) &&
                    Modifier.isStatic(field.getModifiers()) &&
                    field.getType() == Color.class) {

                names.add(field.getName());
            }
        }

        Collections.sort(names);
        return names;
    }
}
