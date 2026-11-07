package scenes.voice;

import entity.voice.Patch;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.voice.subpanes.VoiceDetailDialog;
import util.Util;
import util.app.AppInfo;
import util.midi.MidiHelper;
import util.user.UserData;
import util.voice.InstrumentProvider;
import util.voice.VoiceType;

import java.util.*;

import static util.Constants.*;
import static util.app.colors.ColorScheme.getColor;

public class VoiceListPane extends AnchorPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceListPane.class);

    private MidiHelper midiHelper = MidiHelper.getInstance();

    private final UserData userData = UserData.getInstance();
    private final AppInfo appInfo = AppInfo.INSTANCE;

    public VoiceListPane() {
        super();

        setId("VoiceListPane");
    }

    private final TableView<Patch> table = new TableView<>();

    @Getter
    private final Map<Integer, Patch> channelPatches = new HashMap<>();

    private VoiceResultPane parent;
    public VoiceListPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        channelPatches.clear();
        channelPatches.put(CHANNEL_RIGHT1, new Patch(null, "Live", "Grand Piano", null, "0", "115", "0"));
        channelPatches.put(CHANNEL_LEFT, new Patch(null, "Live", "Grand Piano", null, "0", "115", "0"));
        channelPatches.put(CHANNEL_RIGHT2, new Patch(null, "Live" , "Grand Piano", null, "0", "115", "0"));

        Util.setPaneHeightAsPercentage(this, owner, height);
//        Util.showPaneBorder(this, getColor("border", "green"));

        setTopAnchor(table, 0.0);
        setBottomAnchor(table, 0.0);
        setLeftAnchor(table, 0.0);
        setRightAnchor(table, 0.0);

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        table.setId("VoicesTable");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        createColumns(table);

        setupTableForClicks();
        createContextMenu(table);

        getChildren().add(table);

        logger.debug("built {}", getId());
    }

    private TableColumn<Patch, String> nameColumn;

    private BooleanProperty favoriteDisabled = new SimpleBooleanProperty(true);

    private void createContextMenu(TableView<Patch> table) {
        // add Context Menu here
        ContextMenu contextMenu = new ContextMenu();

        Menu voiceSubMenu = new Menu("Set as ...");

        MenuItem right1 = new MenuItem("Right1");
        right1.setOnAction(e -> {
            Patch patch = table.getSelectionModel().getSelectedItem();
            channelPatches.put(CHANNEL_RIGHT1,patch);
            midiHelper.sendAsMidi(CHANNEL_RIGHT1, patch);
        });
        MenuItem left = new MenuItem("Left");
        left.setOnAction(e -> {
            Patch patch = table.getSelectionModel().getSelectedItem();
            channelPatches.put(CHANNEL_LEFT,patch);
            midiHelper.sendAsMidi(CHANNEL_LEFT, patch);
        });
        MenuItem right2 = new MenuItem("Right2");
        right2.setOnAction(e -> {
            Patch patch = table.getSelectionModel().getSelectedItem();
            channelPatches.put(CHANNEL_RIGHT2,patch);
            midiHelper.sendAsMidi(CHANNEL_RIGHT2, patch);
        });

        voiceSubMenu.getItems().addAll(right1, left, right2);

        MenuItem details = new MenuItem("Show details");
        details.setOnAction(event -> {
            Patch patch = table.getSelectionModel().getSelectedItem();

            InstrumentProvider instrumentProvider = AppInfo.INSTANCE.getInstrumentProvider();
            if (VoiceType.INS.equals(instrumentProvider.getSourceType())) {
                patch = instrumentProvider.findLinkedPatch(patch);
            }

            VoiceDetailDialog dialog = new VoiceDetailDialog(patch);
            dialog.showAndWait();
        });

        Menu favoritesSubMenu = new Menu("Favorites");

        favoritesSubMenu.setOnShowing(event -> {
            Patch selectedPatch = table.getSelectionModel().getSelectedItem();

            if (selectedPatch != null) {
                favoriteDisabled.set(appInfo.getInstrumentProvider().isFavorite(selectedPatch));
            }
        });

        MenuItem add = new MenuItem("Add");
        add.disableProperty().bind(favoriteDisabled);
        add.setOnAction(e -> {
            Patch selectedPatch = table.getSelectionModel().getSelectedItem();

            if (selectedPatch.getLinkedPatch() != null) {
                userData.addAsFavorite(selectedPatch.getLinkedPatch());
            } else {
                userData.addAsFavorite(selectedPatch);
            }

            table.refresh();
        });
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e -> {
            Patch selectedPatch = table.getSelectionModel().getSelectedItem();
            if (selectedPatch.getLinkedPatch() != null) {
                userData.removeFromFavorites(selectedPatch.getLinkedPatch());
            } else {
                userData.removeFromFavorites(selectedPatch);

                if (getVoiceResultPane().getVoiceFilterPane().isFavoritesShown()) {

                    // TODO remove from table if favorties is selected
                    getVoiceResultPane().getVoiceListPane().showResultsNoStorage(new ArrayList<>(UserData.getInstance().getFavorites()));
                    List<Patch> favorites = new ArrayList<>(userData.getFavorites());

                    List<Patch> newData = favorites.stream().filter(patch -> ! excludes.contains(patch.getType())).toList();

                    ObservableList<Patch> tableData = FXCollections.observableArrayList(newData);
                    table.setItems(tableData);

                    autoSizeColumn(table, nameColumn, 70);
                }

            }

            table.refresh();
        });
        remove.disableProperty().bind(favoriteDisabled.not());

        favoritesSubMenu.getItems().addAll(add, remove);

        contextMenu.getItems().addAll(voiceSubMenu, new SeparatorMenuItem(), details, new SeparatorMenuItem(), favoritesSubMenu);

/*        // MenuItem (Details)
        MenuItem details = new MenuItem("Show details");
        details.setOnAction(event -> {
            Patch patch = table.getSelectionModel().getSelectedItem();

            InstrumentProvider instrumentProvider = ApplicationInfo.getInstance().getInstrumentProvider();
            if ("INS".equals(instrumentProvider.getSourceType())) {
                patch = instrumentProvider.findLinkedPatch(patch);
            }

            VoiceDetailDialog dialog = new VoiceDetailDialog(patch);
            dialog.showAndWait();
        });

        // MenuItem (Favorites)
        MenuItem favorites = new MenuItem("Add as favorite");

        contextMenu.getItems().addAll(right1, left, new SeparatorMenuItem(), details, new SeparatorMenuItem(), favorites);

        contextMenu.setOnShowing(event -> {
            Patch selectedPatch = table.getSelectionModel().getSelectedItem();

            if (selectedPatch != null) {
                // Check if the patch is in the ObservableSet
                if (SysData.isFavorite(selectedPatch)) {
                    favorites.setText("Remove from Favorites");
                    favorites.setOnAction(e -> {
                        SysData.removeFromFavorites(selectedPatch);
                        table.refresh(); // Update the star immediately
                    });
                } else {
                    favorites.setText("Add to Favorites");
                    favorites.setOnAction(e -> {
                        SysData.getFavorites().add(selectedPatch);
                        table.refresh(); // Update the star immediately
                    });
                }
            }
        });
*/
        table.setContextMenu(contextMenu);
    }

    private void createColumns(TableView<Patch> table) {
        nameColumn = new TableColumn<>();
        setColumnLabel(nameColumn, "Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);

        /*
         * The Name column also display a star when it is a favorite
         */
        nameColumn.setCellFactory(column -> new TableCell<>() {
            // We make once the icon for a cel for beter performances
            private final Label starIcon = new Label("★");
            {
                starIcon.setTextFill(Color.GOLD); // GOLD looks better
                starIcon.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    // Retrieve the Patch object from the current row
                    Patch currentPatch = getTableView().getItems().get(getIndex());

                    // is it a favortie?
                    if (appInfo.getInstrumentProvider().isFavorite(currentPatch)) {
                        // Place the icon right of the text
                        setContentDisplay(ContentDisplay.RIGHT);
                        setGraphic(starIcon);
                    } else {
                        setGraphic(null); // Remove the star if it is no favorite
                    }
                }
            }
        });

        TableColumn<Patch, String> typeColumn = new TableColumn<>();
        setColumnLabel(typeColumn, "Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        table.getColumns().add(typeColumn);

        TableColumn<Patch, Integer> bankColumn  = new TableColumn<>();
        setColumnLabel(bankColumn, "Bank");
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bank"));
        table.getColumns().add(bankColumn);

        TableColumn<Patch, Integer> msbColumn  = new TableColumn<>();
        setColumnLabel(msbColumn, "MSB");
        msbColumn.setCellValueFactory(new PropertyValueFactory<>("msb"));
        table.getColumns().add(msbColumn);

        TableColumn<Patch, Integer> lsbColumn  = new TableColumn<>();
        setColumnLabel(lsbColumn, "LSB");
        lsbColumn.setCellValueFactory(new PropertyValueFactory<>("lsb"));
        table.getColumns().add(lsbColumn);

        TableColumn<Patch, Integer> pcColumn  = new TableColumn<>();
        setColumnLabel(pcColumn, "PC");
        pcColumn.setCellValueFactory(new PropertyValueFactory<>("pc"));
        table.getColumns().add(pcColumn);
    }

    private <T> void setColumnLabel(TableColumn<Patch, T> column, String label) {
        Label columnLabel = new Label(label);
        columnLabel.setMaxWidth(Double.MAX_VALUE); // Forces the label to span the whole column width
        columnLabel.setAlignment(Pos.CENTER_LEFT);  // Aligns text left
        column.setGraphic(columnLabel);
    }

    private List<Patch> currentData = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();
    private List<Patch> oldData;
    // TODO Solve problem: display favorites and select then INS
    public void showResults (List<Patch> data, boolean areFavorites) {
        List<Patch> tableData = null;

        if (data != null && !data.isEmpty()) {
            if (getVoiceResultPane().getVoiceFilterPane().isFavoritesShown()) {
                if (areFavorites) {
                    oldData = currentData;
                    currentData = data;
                    tableData = currentData;
                } else {
                    oldData = data;
                    tableData = currentData;
                }
            } else {
                if (! areFavorites) {
                    currentData = data;
                }
            }
        } else {
            if (data == null) {
                if (areFavorites && getVoiceResultPane().getVoiceFilterPane().isFavoritesShown()) {
                    currentData = data;
                } else {
                    currentData = oldData;
                }
            } else {
                if (areFavorites) {
                    oldData = currentData;
                    currentData = data;
                    System.out.println("data is empty");
                }
            }
        }

        if (currentData != null) {
            List<Patch> newData = currentData.stream().filter(patch -> !excludes.contains(patch.getType())).toList();

            table.setItems(FXCollections.observableArrayList(newData));

            autoSizeColumn(table, nameColumn, 70);

            table.refresh();
        }
    }

    public void showResultsNoStorage (List<Patch> data) {
        List<Patch> newData = data.stream().filter(patch -> ! excludes.contains(patch.getType())).toList();

        ObservableList<Patch> tableData = FXCollections.observableArrayList(newData);
        table.setItems(tableData);

        autoSizeColumn(table, nameColumn, 70);
        table.refresh();
    }

    private void setupTableForClicks() {
        table.setRowFactory(tv -> {
            TableRow<Patch> row = new TableRow<>();

            // Capture the key modifiers right as the click happens
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {

                    // 1. Snapshot the modifiers
                    boolean shift = event.isShiftDown();
                    boolean ctrl = event.isControlDown();

                    // 2. Wait a split second for JavaFX to process the selection list
                    Platform.runLater(() -> {
                        ObservableList<Patch> selectedItems = table.getSelectionModel().getSelectedItems();

                        // 3. CRITICAL: Only proceed if exactly ONE item is currently selected
                        if (selectedItems.size() == 1) {

                            int targetChannel;
                            if (shift) {
                                targetChannel = CHANNEL_LEFT; // Channel 2 (Shift click)
                            } else if (ctrl) {
                                targetChannel = CHANNEL_RIGHT2; // Channel 3 (Ctrl click)
                            } else {
                                targetChannel = CHANNEL_RIGHT1; // Channel 1 (Normal click)
                            }

                            Patch singleItem = selectedItems.get(0);
                            channelPatches.put(targetChannel,singleItem);
                            midiHelper.sendAsMidi(targetChannel, singleItem);
                        }
                    });
                }
            });

            return row;
        });
    }

    public void setExcludedVoiceTypes(List<String> excludedVoiceTypes) {
        this.excludes =  excludedVoiceTypes;
        if (getVoiceResultPane().getVoiceFilterPane().isFavoritesShown()) {
            showResults(oldData, false);
        } else {
            showResults(currentData, false);
        }
    }

    private static final Map<TableColumn<?, ?>, Double> originalWidths =
            new WeakHashMap<>();

    public static <T> void autoSizeColumn(
            TableView<T> table,
            TableColumn<T, String> nameColumn,
            double widthPercent) {

        Platform.runLater(() -> {

            var columns = table.getVisibleLeafColumns();

            double totalWidth = table.getWidth() - 2;

            if (totalWidth <= 0) return;

            // Save original widths only once
            for (TableColumn<T, ?> col : columns) {
                if (col != nameColumn) {
                    originalWidths.putIfAbsent(col, col.getWidth());
                }
            }

            // Original width of all other columns
            double otherWidth = 0;

            for (TableColumn<T, ?> col : columns) {
                if (col != nameColumn) {
                    otherWidth += originalWidths.get(col);
                }
            }

            // Original available width for nameColumn
            double originalNameWidth = totalWidth - otherWidth;

            // Apply percentage to ORIGINAL available width
            double nameWidth = originalNameWidth *
                    Math.max(0, Math.min(100, widthPercent)) / 100.0;

            nameWidth = Math.max(
                    nameColumn.getMinWidth(), nameWidth
            );

            // Remaining width for other columns
            double remainingWidth = totalWidth - nameWidth;

            nameColumn.setPrefWidth(nameWidth);

            // Redistribute remaining space proportionally
            if (otherWidth > 0) {
                for (TableColumn<T, ?> col : columns) {
                    if (col == nameColumn) continue;

                    double originalWidth = originalWidths.get(col);

                    double newWidth =
                            remainingWidth * originalWidth / otherWidth;

                    col.setPrefWidth(newWidth);
                }
            }
        });
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
