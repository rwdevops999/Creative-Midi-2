package scenes.merge_upload;

import fx.component.ImageButton;
import fx.dialog.DialogFactory;
import fx.dialog.NameDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.keyboards.yamaha.KeyboardUtil;
import util.keyboards.yamaha.StyleMerger;
import util.registry.Registry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private StringProperty styleFileProperty = new SimpleStringProperty("");
    private StringProperty midiFileProperty = new SimpleStringProperty("");

    private boolean styleSelected = false;
    private boolean midiSelected = false;

    private String selectedKeyboardStyleFile = null;
    private String selectedKeyboardMidiFile = null;

    private static final int VIEW_HEIGHT = 175;

    private List<String> styleFiles;
    private List<String> midiFiles;

    private void buildPane() {
        logger.debug("Building {}", getId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        int row = 0;

        ListView<String> stylesListView = new ListView<>();
        stylesListView.setMaxWidth(Double.MAX_VALUE);
        stylesListView.setMaxHeight(VIEW_HEIGHT);
        stylesListView.setPrefHeight(VIEW_HEIGHT);
        add (stylesListView, 0, row, 10, 3);
        setStyleFiles(stylesListView);

        stylesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               selectedKeyboardStyleFile = newValue;
           }
        });

        // style uploading
        row += 3;

        Button loadStyleFileButton = new Button("Load Style ...");
        loadStyleFileButton.setOnAction(event -> {
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("STYLE", "*.sty")
            );

            if (AppInfo.INSTANCE.getWorkingDir() != null) {
                fileChooser.setInitialDirectory(new File(AppInfo.INSTANCE.getWorkingDir()));
            }
            File selectedFile = fileChooser.showOpenDialog(AppInfo.INSTANCE.getPrimaryStage());
            styleSelected = false;
            if (selectedFile != null) {
                styleFileProperty.set(selectedFile.getAbsolutePath());
                styleSelected = true;
            } else {
                styleFileProperty.set(null);
            }
            styleUploadPropertyDisabled.set(!styleSelected);
            handleMergeDisabling();
        });
        add (loadStyleFileButton, 0, row);

        Label styleFilenameLabel = new Label();
        styleFilenameLabel.textProperty().bind(styleFileProperty);
        add (styleFilenameLabel, 2, row, 5, 1);

        ImageButton uploadStyleButton = new ImageButton("icons/upload-up.png", 100, 35);
        uploadStyleButton.disableProperty().bindBidirectional(styleUploadPropertyDisabled);
        add (uploadStyleButton, 8, row, 2, 1);

        uploadStyleButton.setOnAction(event -> {
            NameDialog nameDialog = new NameDialog("style", selectedKeyboardStyleFile);
            Optional<String> styleName = nameDialog.showAndWait();
            if (styleName.isPresent()) {
                if (styleFiles.contains(styleName.get())) {
                    if (DialogFactory.getInstance().renderConfirmationDialog("File exists ...", "Overwrite ?")) {
                        uploadStyleFile(selectedKeyboardStyleFile, styleFileProperty.get());
                    }
                } else {
                    uploadStyleFile(styleName.get(), styleFileProperty.get());
                }
            }
        });

        // merge button
        row++;
        ImageButton mergeButton = new ImageButton("icons/merge.png", 100, 100);
        mergeButton.disableProperty().bindBidirectional(mergePropertyDisabled);
        mergeButton.setOnAction(event -> {
            handleMerge(styleFileProperty.get(), midiFileProperty.get());
        });
        add (mergeButton, 8, row, 2, 2);

        // midi uploading
        row += 2;

        Button loadMidiFileButton = new Button("Load Midi ...");
        loadMidiFileButton.setOnAction(event -> {
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MIDI", "*.mid")
            );

            if (AppInfo.INSTANCE.getWorkingDir() != null) {
                fileChooser.setInitialDirectory(new File(AppInfo.INSTANCE.getWorkingDir()));
            }

            File selectedFile = fileChooser.showOpenDialog(AppInfo.INSTANCE.getPrimaryStage());
            midiSelected = false;
            if (selectedFile != null) {
                midiFileProperty.set(selectedFile.getAbsolutePath());
                midiSelected = true;
            } else {
                midiFileProperty.set(null);
            }
            midiUploadPropertyDisabled.set(!midiSelected);
            handleMergeDisabling();
        });
        add (loadMidiFileButton, 0, row);

        Label midiFilenameLabel = new Label();
        midiFilenameLabel.textProperty().bind(midiFileProperty);
        add (midiFilenameLabel, 2, row, 5, 1);

        ImageButton uploadMidiButton = new ImageButton("icons/upload-down.png", 100, 35);
        uploadMidiButton.disableProperty().bindBidirectional(midiUploadPropertyDisabled);
        add (uploadMidiButton, 8, row, 2, 1);
        uploadMidiButton.setOnAction(event -> {
            NameDialog nameDialog = new NameDialog("style", selectedKeyboardMidiFile);
            Optional<String> songName = nameDialog.showAndWait();
            if (songName.isPresent()) {
                if (midiFiles.contains(songName.get())) {
                    if (DialogFactory.getInstance().renderConfirmationDialog("File exists ...", "Overwrite ?")) {
                        uploadMidiFile(selectedKeyboardMidiFile, midiFileProperty.get());
                    }
                } else {
                    uploadMidiFile(songName.get(), midiFileProperty.get());
                }
            }
        });

        // midis list
        row++;

        ListView<String> midisListView = new ListView<>();
        midisListView.setMaxWidth(Double.MAX_VALUE);
        midisListView.setMaxHeight(VIEW_HEIGHT);
        midisListView.setPrefHeight(VIEW_HEIGHT);
        add (midisListView, 0, row, 10, 3);

        setMidiFiles(midisListView);

        midisListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedKeyboardMidiFile = newValue;
            }
        });

        Registry.getInstance().showStatus("Uploaded data from keyboard");

        logger.debug("Built {}", getId());
    }

    private void handleMergeDisabling() {
        mergePropertyDisabled.set(!(styleSelected && midiSelected));
    }

    private void setStyleFiles(ListView<String> listView) {
        try {
            KeyboardUtil utils = new KeyboardUtil();
            styleFiles = utils.getStyleFiles().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
            if (styleFiles == null) {
                styleFiles = new ArrayList<>();
            }
            listView.getItems().addAll(styleFiles);
        } catch (Exception e) {
            styleFiles = new ArrayList<>();
            listView.getItems().addAll(styleFiles);
        }
    }

    private void setMidiFiles(ListView<String> listView) {
        try {
            KeyboardUtil utils = new KeyboardUtil();
            midiFiles = utils.getSongFiles().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
            listView.getItems().addAll(midiFiles);
        } catch (Exception e) {
            listView.getItems().addAll(new ArrayList<>());
        }
    }


        private void uploadStyleFile(String styleName, String selectedStyleFile) {
            KeyboardUtil utils = new KeyboardUtil();

            utils.uploadStyle(selectedStyleFile, styleName);
        }

        private void uploadMidiFile(String songName, String selectedMidiFile) {
            KeyboardUtil utils = new KeyboardUtil();

            utils.uploadSong(selectedMidiFile, songName);
        }

        private void handleMerge(String styleFileName, String midiFileName) {
            StyleMerger styleMerger = new StyleMerger();
            String newStyleFilename = styleMerger.mergeStyleAndMidi(styleFileName, midiFileName);
            styleFileProperty.set(newStyleFilename);
        }
}
