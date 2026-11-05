package fx.dialog.content;

import entity.midi.MidiEventInfo;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import static util.Util.toPascalCase;

public class EventDetailPane extends GridPane {
    public EventDetailPane() {
        super();

        int[] columnSizes = {1,10,1,10,5,10,1,10,5,10,1,10,5,10,1,10};
        for (int size: columnSizes) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(size);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraint);

        setVgap(5);
    }

    public EventDetailPane(MidiEventInfo eventInfo) {
        this();

        buildPane(eventInfo);
    }

    private void buildPane(MidiEventInfo eventInfo) {
        String firstElement = eventInfo.getRootEventKey().name();
        String kind = makeReadable(firstElement, null);
        String type = makeReadable(eventInfo.getEventEventKey().name(), firstElement);
        String comment = eventInfo.getComment();
        String message = eventInfo.getMessage();

        String mbtPosition = eventInfo.getMbtPosition();
        Integer value = eventInfo.getValue();
        Integer channel = eventInfo.getChannel();

        int row = 0;
        // ROW 0 (Type)
        Label kindLabel = new Label("Kind:");
        add(kindLabel, 1, row, 3, 1);

        Label kindValueLabel = new Label(kind + " event");
        kindValueLabel.setStyle("-fx-font-weight: bold;");
        add(kindValueLabel, 4, row, 12, 1);

        // ROW 1 (type)
        row++;
        Label typeLabel = new Label("Type:");
        add(typeLabel, 1, row, 3, 1);

        Label typeValueLabel = new Label(type);
        typeValueLabel.setStyle("-fx-font-weight: bold;");
        add(typeValueLabel, 4, row, 12, 1);

        // ROW 2 (Comment)
        if (comment != null) {
            row++;
            Label commentLabel = new Label("Comment:");
            add(commentLabel, 1, row, 3, 1);

            Label commentValueLabel = new Label(comment);
            commentValueLabel.setStyle("-fx-font-weight: bold;");
            add(commentValueLabel, 4, row, 12, 1);
        }

        // ROW 3
        if (value != null) {
            row++;

            Label valueLabel = new Label("Value:");
            add(valueLabel, 1, row, 3, 1);

            Label valueValueLabel = new Label(""+value);
            valueValueLabel.setStyle("-fx-font-weight: bold;");
            add(valueValueLabel, 4, row, 12, 1);
        }

        // ROW4
        row++;
        Label channelLabel = new Label("Channel:");
        add(channelLabel, 1, row, 3, 1);
        Label channelValueLabel;
        if (channel != null) {
            channelValueLabel = new Label("Channel " + channel);
        } else {
            channelValueLabel = new Label("No channel");
        }
        channelValueLabel.setStyle("-fx-font-weight: bold;");
        add(channelValueLabel, 4, row, 12, 1);
        // ROW 3 (SEPARATOR)
        row++;
        Label separator = new Label(" ");
        add(separator, 1, row, 16, 1);

        // ROW 4 (Message)
        row += 2;
        Label messageLabel = new Label("Message:");
        add(messageLabel, 1, row, 3, 1);

        TextField messageValue = new TextField(message);
        messageValue.setTextFormatter(new TextFormatter<>(change -> {
            // Allow programmatic initial text setup, but block user edits
            if (change.isContentChange()) {
                return null;
            }
            return change;
        }));
        add(messageValue, 4, row, 12, 1);
    }

    private String makeReadable(String value1, String value2) {
        String result;

        if (value2 == null) {
            result = toPascalCase(value1);
        } else {
            result = toPascalCase(value1.replace(value2 + "_", "").replace("_", " "));
        }

        return result;
    }
}
