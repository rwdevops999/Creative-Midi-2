package app.root.root.header;

import entity.DeviceInfo;
import fx.component.LabeledSelector;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.properties.AppProperties;
import util.properties.PropertyKeys;
import util.registry.AppEvent;
import util.registry.Registry;
import java.util.List;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;
import static util.Util.showPaneBorder;

public class SelectorsPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(SelectorsPane.class);

    public SelectorsPane() {
        super();

        setId("SelectorsPane");

        setSpacing(0);
        setAlignment(Pos.TOP_LEFT);
    }

    private HeaderPane parent;
    public SelectorsPane(HeaderPane owner, int width, int height) {
        this();

        parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);
        showPaneBorder(this, getColor("border", "blue"));

        buildPane();
    }

    private DeviceInfo lastNotifiedDevice = null;
    private boolean isUpdatingList = false;
    private boolean isFirstLoad = true;

    private void buildPane() {
        LabeledSelector<DeviceInfo> deviceSelector = new LabeledSelector<>(parent, "Device");

        deviceSelector.prefWidthProperty().bind(this.widthProperty().divide(2));

        getChildren().add(deviceSelector);

        deviceSelector.getComboBox().getSelectionModel().selectedItemProperty().addListener((obs, oldDevice, newDevice) -> {
            if (isUpdatingList) return; // Negeren tijdens achtergrond updates van de lijst

            if (newDevice != null) {
                AppInfo.INSTANCE.setSelectedDevice(newDevice);

                if (lastNotifiedDevice == null || !lastNotifiedDevice.getName().equals(newDevice.getName())) {
                    showNotification("Apparaat Actief", "Apparaat '" + newDevice.getName() + "' is nu actief.");
                    lastNotifiedDevice = newDevice;
                }
            } else {
                AppInfo.INSTANCE.setSelectedDevice(null);
                lastNotifiedDevice = null;
            }
        });

        Registry.getInstance().register(event -> {
            if (event != null && AppEvent.AppEventType.DEVICE.equals(event.type())) {
                if (event.data() instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<DeviceInfo> deviceList = (List<DeviceInfo>) event.data();

                    Platform.runLater(() -> {
                        var comboBox = deviceSelector.getComboBox();

                        DeviceInfo currentlySelected = AppInfo.INSTANCE.getSelectedDevice();
                        String defaultDeviceName = AppProperties.getInstance().getPropertyAsString(PropertyKeys.DEVICE, null);

                        try {
                            isUpdatingList = true; // Blokkeer de listener tijdens het verversen van de lijst
                            comboBox.getItems().setAll(deviceList);

                            DeviceInfo deviceToSelect = null;

                            // SCENARIO A: Eerste keer opstarten -> Zoek de default uit de properties
                            if (isFirstLoad) {
                                if (defaultDeviceName != null) {
                                    deviceToSelect = deviceList.stream()
                                            .filter(d -> d.getName().equals(defaultDeviceName))
                                            .findFirst()
                                            .orElse(null);
                                }
                                isFirstLoad = false;
                            }
                            // SCENARIO B: Normale werking -> Behoud het apparaat dat actief was
                            else if (currentlySelected != null) {
                                deviceToSelect = deviceList.stream()
                                        .filter(d -> d.getName().equals(currentlySelected.getName()))
                                        .findFirst()
                                        .orElse(null);

                                // EENMALIGE MELDING: Het actieve apparaat is zojuist UITGESCHAKELD (niet meer in de lijst)
                                if (deviceToSelect == null) {
                                    showNotification("Apparaat Verbroken",
                                            "Het apparaat '" + currentlySelected.getName() + "' is uitgeschakeld of losgekoppeld.");
                                }
                            }

                            // UITVOERING VAN DE SELECTIE IN DE UI
                            if (deviceToSelect != null) {
                                comboBox.getSelectionModel().select(deviceToSelect);
                                AppInfo.INSTANCE.setSelectedDevice(deviceToSelect);

                                // Geef melding als er een nieuw apparaat actief is geworden
                                if (lastNotifiedDevice == null || !lastNotifiedDevice.getName().equals(deviceToSelect.getName())) {
                                    showNotification("Apparaat Actief", "Apparaat '" + deviceToSelect.getName() + "' is nu actief.");
                                    lastNotifiedDevice = deviceToSelect;
                                }
                            } else {
                                // HIER GEBEURT HET: Als het apparaat uit staat, wordt de combo leeg gemaakt.
                                // Er wordt GEEN ander apparaat (zoals Gervill) gekozen.
                                comboBox.getSelectionModel().clearSelection();
                                AppInfo.INSTANCE.setSelectedDevice(null);
                                lastNotifiedDevice = null;
                            }

                        } finally {
                            isUpdatingList = false; // Listener weer aanzetten voor handmatige UI-acties
                        }
                    });
                }
            }
        });

    /*
    Als je straks de keyboardSelector aanzet, krijgt die ook 50% via exact dezelfde regel:
    LabeledSelector keyboardSelector = new LabeledSelector(parent, "Keyboard");
    keyboardSelector.prefWidthProperty().bind(this.widthProperty().divide(2));
    getChildren().add(keyboardSelector);
    */
    }

    private void showNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(3)) // Automatically hides after 5 seconds
                .position(Pos.TOP_LEFT)     // Set corner position on screen
                .showInformation();
    }

    // ACCESSORS
    public HeaderPane getHeaderPane() {
        return this.parent;
    }
}
