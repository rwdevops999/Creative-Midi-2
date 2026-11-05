package scenes.voice;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;
import util.app.AppInfo;
import util.voice.InstrumentProvider;

import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchSelectionPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchSelectionPane.class);

    public VoiceSearchSelectionPane() {
        super();

        setId("VoiceSearchSelectionPane");
    }

    private VoiceSearchPane parent;
    public VoiceSearchSelectionPane(VoiceSearchPane owner, double height) {
        this();

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "red"));

        this.parent = owner;

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        TabPane tabPane = new TabPane();
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
/*            if (selectedTabIndex.get() != newIdx.intValue()) {
                selectedTIndex = newIdx.intValue();
                selectedTabIndex.set(newIdx.intValue());
                handleSearch(true);
            } */
        });

        Tab tab1 = new Tab("Patch");
        VBox tab1Content = new VBox();
//        buildDynamicSearchPane(tab1Content);
        tab1.setContent(tab1Content);

        InstrumentProvider provider = AppInfo.INSTANCE.getInstrumentProvider();

        Tab tab2 = new Tab(provider.getGroupingName());
        VBox tab2Content = new VBox();
//        buildBankSearchPane(tab2Content);
        tab2.setContent(tab2Content);

        Tab tab3 = new Tab("Detail");
        VBox tab3Content = new VBox();
//        buildBankSearchPane(tab2Content);
        tab2.setContent(tab3Content);

        tabPane.getTabs().addAll(tab1, tab2, tab3);

        getChildren().add(tabPane);

        logger.debug("built {}", getId());
    }

    private void buildBankSearchPane(VBox pane) {
        if (! pane.getChildren().isEmpty()) {
            pane.getChildren().clear();
        }

        pane.setSpacing(10);

        pane.setPadding(new Insets(5, 0, 0, 0));

/*        TitleSearchPane titleSearchPane = new TitleSearchPane(this, "Patch Values");
        pane.getChildren().add(titleSearchPane);

        Button searchButton = titleSearchPane.getSearchButton();

        ValuePane valuePane = new ValuePane(this, "Bank", "0");
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "MSB", "0");
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "LSB", "0");
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "PC", null);
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        for (ValuePane p : valuePanes) {
            pane.getChildren().add(p);
        }

        setupValuePanes(); */
        Label tab3Label = new Label("Tab3");
        pane.getChildren().add(tab3Label);
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return parent;
    }
}
