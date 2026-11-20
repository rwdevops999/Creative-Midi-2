package scenes.merge_upload;

import fx.component.ImageButton;
import fx.dialog.DialogFactory;
import fx.dialog.NameDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.keyboards.yamaha.KeyboardUtil;
import util.keyboards.yamaha.StyleMerger;
import util.midi.MidiSplitter;
import util.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
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

    private ListView<String> stylesListView;
    private ProgressBar uploadProgressBar;

    private void buildPane() {
        logger.debug("Building {}", getId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        int row = 0;

        stylesListView = new ListView<>();
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
        styleFilenameLabel.textProperty().bind(
                styleFileProperty.map(path -> path != null ? FilenameUtils.getBaseName(path) : "")
        );
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
        midiFilenameLabel.textProperty().bind(
                midiFileProperty.map(path -> path != null ? FilenameUtils.getBaseName(path) : "")
        );
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
        row += 2;

        ListView<String> midisListView = new ListView<>();
        midisListView.setMaxWidth(Double.MAX_VALUE);
        midisListView.setMaxHeight(VIEW_HEIGHT);
//        midisListView.setMinHeight(VIEW_HEIGHT);
        midisListView.setPrefHeight(VIEW_HEIGHT);
        add (midisListView, 0, row, 10, 3);

        setMidiFiles(midisListView);

        midisListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedKeyboardMidiFile = newValue;
            }
        });

        uploadProgressBar = new ProgressBar(0);
        uploadProgressBar.setPrefSize(300, 25);
        uploadProgressBar.setMaxSize(300, 25);
        uploadProgressBar.setVisible(false);

        GridPane.setHalignment(uploadProgressBar, HPos.CENTER);
        GridPane.setValignment(uploadProgressBar, VPos.CENTER);
        add(uploadProgressBar, 0, 0, 10, 10);

//        Registry.getInstance().showStatus("Uploaded data from keyboard");

        logger.debug("Built {}", getId());
    }

    private void handleMergeDisabling() {
        mergePropertyDisabled.set(!(styleSelected && midiSelected));
    }

    private void setStyleFiles(ListView<String> listView) {
        try {
            KeyboardUtil utils = new KeyboardUtil();
            styleFiles = utils.getStyleFiles().stream()
                    .sorted(
                            Comparator.comparing((String s) -> s.matches("^\\d+.*") ? 0 : 1) // 1. Numbered files first
                                    .thenComparing(s -> s.matches("^\\d+.*")
                                            ? new BigInteger(s.replaceAll("^(\\d+).*", "$1"))
                                            : BigInteger.ZERO)                                       // 2. Sort by leading number
                                    .thenComparing(String.CASE_INSENSITIVE_ORDER)                // 3. Sort alphabetically
                    )
                    .toList();
            listView.getItems().clear();
            listView.getItems().addAll(styleFiles);
        } catch (Exception e) {
            styleFiles = new ArrayList<>();
            listView.getItems().clear();
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
            Task<Void> uploadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    KeyboardUtil utils = new KeyboardUtil();

                    utils.uploadStyle(selectedStyleFile, styleName, this::updateProgress);

                    return null;
                }
            };

            uploadProgressBar.progressProperty().bind(uploadTask.progressProperty());
            uploadProgressBar.visibleProperty().bind(uploadTask.runningProperty());

            this.disableProperty().bind(uploadTask.runningProperty());

            uploadTask.setOnSucceeded(e -> {
                this.disableProperty().unbind();
                this.setDisable(false);

                Registry.getInstance().showStatus("Upload Success");

                setStyleFiles(stylesListView);
            });

            uploadTask.setOnFailed(e -> {
                this.disableProperty().unbind();
                this.setDisable(false);

                Throwable ex = uploadTask.getException();
                logger.error("Upload failed: {}", ex.getMessage());
                Registry.getInstance().showStatus("Upload failed: " + ex.getMessage());
            });

            Thread uploadThread = new Thread(uploadTask);
            uploadThread.setDaemon(true);
            uploadThread.start();
        }

        private void uploadMidiFile(String songName, String selectedMidiFile) {
            Task<Void> uploadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    KeyboardUtil utils = new KeyboardUtil();

                    utils.uploadSong(selectedMidiFile, songName, this::updateProgress);

                    return null;
                }
            };

            uploadProgressBar.progressProperty().bind(uploadTask.progressProperty());
            uploadProgressBar.visibleProperty().bind(uploadTask.runningProperty());

            this.disableProperty().bind(uploadTask.runningProperty());

            uploadTask.setOnSucceeded(e -> {
                this.disableProperty().unbind();
                this.setDisable(false);

                Registry.getInstance().showStatus("Upload Success");
                // Optioneel: ververs hier je styleFiles / midiFiles ListView
            });

            uploadTask.setOnFailed(e -> {
                this.disableProperty().unbind();
                this.setDisable(false);

                Throwable ex = uploadTask.getException();
                logger.error("Upload failed: {}", ex.getMessage());
                Registry.getInstance().showStatus("Upload failed: " + ex.getMessage());
            });

            Thread uploadThread = new Thread(uploadTask);
            uploadThread.setDaemon(true);
            uploadThread.start();
        }

        private void handleMerge(String styleFileName, String midiFileName) {
            StyleMerger merger = new StyleMerger();
            try {
                String mergedStyleFile = merger.merge(styleFileName, midiFileName);
                styleFileProperty.set(mergedStyleFile);
            } catch (Exception e) {
                logger.error("Exception. CAUSE: {}", e.getMessage());
            }
    }
}
