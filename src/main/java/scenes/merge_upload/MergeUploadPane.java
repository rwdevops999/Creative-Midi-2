package scenes.merge_upload;

import entity.NameEntity;
import entity.playlist.Song;
import fx.component.ImageButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.SceneActionsPane;
import scenes.sysex.SysexDetailsPane;
import scenes.sysex.SysexListPane;
import scenes.sysex.consumers.*;
import scenes.sysex.statemachine.StateMachine;
import scenes.sysex.statemachine.States;

import java.util.function.BiConsumer;

import static util.Util.setPaneBackground;

public class MergeUploadPane extends GridPane {
    private static final Logger logger = LoggerFactory.getLogger(MergeUploadPane.class);

    public MergeUploadPane() {
        super();

        setId("MergeUploadPane");

        int totalColumns = 10;
        double percentagePerColumn = 100.0 / totalColumns; // Dit is ~8.3333%

        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentagePerColumn);
            col.setHalignment(HPos.LEFT);
            getColumnConstraints().add(col);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        getRowConstraints().add(rowConstraints);

        setPaneBackground(this);

        setVgap(10);

        setPadding(new Insets(5));

        buildPane();
    }

    private BooleanProperty styleUploadPropertyDisabled = new SimpleBooleanProperty(true);
    private BooleanProperty midiUploadPropertyDisabled = new SimpleBooleanProperty(true);
    private BooleanProperty mergePropertyDisabled = new SimpleBooleanProperty(true);

    private boolean styleSelected = false;
    private boolean midiSelected = false;

    private static final int VIEW_HEIGHT = 175;

    private void buildPane() {
        logger.debug("Building {}", getId());

        int row = 0;

        ListView<String> stylesListView = new ListView();
        stylesListView.setMaxWidth(Double.MAX_VALUE);
        stylesListView.setMaxHeight(VIEW_HEIGHT);
        stylesListView.setPrefHeight(VIEW_HEIGHT);
        add (stylesListView, 0, row, 10, 3);
        setStyleFiles(stylesListView);

        // style uploading
        row += 3;

        Button loadStyleFileButton = new Button("Load Style ...");
        add (loadStyleFileButton, 0, row);

        Label styleFilenameLabel = new Label("Here comes the style file name");
        add (styleFilenameLabel, 2, row, 5, 1);

        ImageButton uploadStyleButton = new ImageButton("icons/upload-up.png", 100, 35);
        uploadStyleButton.disableProperty().bindBidirectional(styleUploadPropertyDisabled);
        add (uploadStyleButton, 8, row, 2, 1);

        // merge button
        row++;
        ImageButton mergeButton = new ImageButton("icons/merge.png", 100, 100);
        mergeButton.disableProperty().bindBidirectional(mergePropertyDisabled);
        add (mergeButton, 8, row, 2, 2);

        // midi uploading
        row += 2;

        Button loadMidiFileButton = new Button("Load Midi ...");
        add (loadMidiFileButton, 0, row);

        Label midiFilenameLabel = new Label("Here comes the midi file name");
        add (midiFilenameLabel, 2, row, 5, 1);

        ImageButton uploadMidiButton = new ImageButton("icons/upload-down.png", 100, 35);
        uploadMidiButton.disableProperty().bindBidirectional(midiUploadPropertyDisabled);
        add (uploadMidiButton, 8, row, 2, 1);

        // midis list
        row++;

        ListView<String> midisListView = new ListView();
        midisListView.setMaxWidth(Double.MAX_VALUE);
        midisListView.setMaxHeight(VIEW_HEIGHT);
        midisListView.setPrefHeight(VIEW_HEIGHT);
        add (midisListView, 0, row, 10, 3);

        setMidiFiles(midisListView);

        logger.debug("Built {}", getId());
    }

    private void setStyleFiles(ListView<String> listView) {
        listView.getItems().add("Style1");
    }

    private void setMidiFiles(ListView<String> listView) {
        listView.getItems().add("Midi1");
    }
}
