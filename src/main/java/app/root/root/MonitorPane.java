package app.root.root;

import entity.monitor.ColoredItem;
import fx.display.ColoredListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.eventhandlers.monitoring.MonitorExportHandler;
import util.registry.AppEvent;
import util.registry.Registry;

import java.util.List;

import static util.Constants.MONITOR_ACTION_CLEAR;
import static util.Util.*;
import static util.communication.CommunicationType.ACTION;

public class MonitorPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(MonitorPane.class);

    public MonitorPane() {
        super();

        setId("MonitorPane");
    }

    private RootPane parent;
    public MonitorPane(RootPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, parent, width, height);
        setPaneBackground(this);

        buildPane();
    }

    private ListView<ColoredItem> messagesView;
    private final ObservableList<ColoredItem> messages = FXCollections.observableArrayList();

    private static final ObjectProperty<AppEvent> messageProperty = new SimpleObjectProperty<>();

    private void buildPane() {
        logger.info("building Pane {}", getId());

        messagesView = new ListView<>();
        messagesView.setCellFactory(lv -> new ColoredListCell());
        messagesView.setItems(messages);
        getChildren().add(messagesView);

        Registry.getInstance().register(event -> {
            if (event != null && AppEvent.AppEventType.MONITOR.equals(event.type())) {
                ColoredItem coloredItem = (ColoredItem) event.data();
                if (coloredItem.getType().equals(ACTION)) {
                    handleMonitorEvent(event);
                } else {
                    messages.add(coloredItem);
                    messagesView.scrollTo(messages.size() - 1);
                    messageProperty.set(event);
                }
            }
        });

        addContextMenu();

        // Hidden
        messageProperty.addListener((observable, oldValue, newValue) -> {
            System.out.println("You need to do something");
            handleMonitorEvent(newValue);
        });

        logger.info("built Pane {}", getId());
    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clearMonitor = new MenuItem("Clear");
        clearMonitor.setOnAction(e -> messagesView.getItems().clear());
        MenuItem exportMonitor = new MenuItem("Export");
        exportMonitor.setOnAction(e -> {
            MonitorExportHandler handler = new MonitorExportHandler();
            handler.handle(List.copyOf(messages));
        });
        contextMenu.getItems().addAll(clearMonitor, exportMonitor);
        messagesView.setContextMenu(contextMenu);
    }

    private void handleMonitorEvent(AppEvent event) {
        ColoredItem coloredItem = (ColoredItem) event.data();

        switch(coloredItem.getType()) {
            case ACTION:
                executeAction(coloredItem.getReadableText());
                break;
            default:
                break;
        }
    }

    private void executeAction(String action) {
        if (action.equals(MONITOR_ACTION_CLEAR)) {
            messages.clear();
            messagesView.getItems().clear();
        }
    }

    // ACCESSORS
    public RootPane getRootPane() {
        return parent;
    }
}
