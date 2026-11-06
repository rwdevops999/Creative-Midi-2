package scenes.voice;

import fx.component.DynamicPane;
import fx.component.GroupPane;
import fx.component.TitledSearchPane;
import fx.component.ValuePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;
import util.app.AppInfo;
import util.voice.InstrumentProvider;

import java.util.ArrayList;
import java.util.List;

import static util.app.colors.ColorScheme.getColor;

public class VoiceSearchSelectionPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(VoiceSearchSelectionPane.class);

    public VoiceSearchSelectionPane() {
        super();

        setId("VoiceSearchSelectionPane");

        setSpacing(5);
        setPadding(new Insets(5));
        setAlignment(Pos.TOP_LEFT);
    }

    private VoiceSearchPane parent;
    public VoiceSearchSelectionPane(VoiceSearchPane owner, double height) {
        this();

        dynamicPanes.add(new DynamicPane(this, 0));
        dynamicPanes.add(null);
        dynamicPanes.add(null);
        dynamicPanes.add(null);
        dynamicPanes.add(null);

        Util.setPaneSizeAsPercentage(this, owner, 100, height);
        Util.showSmallPaneBorder(this, getColor("border", "orange"));

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
        buildDynamicSearchPane(tab1Content);
        tab1.setContent(tab1Content);

        InstrumentProvider provider = AppInfo.INSTANCE.getInstrumentProvider();

        Tab tab2 = new Tab(provider.getGroupingName());
        VBox tab2Content = new VBox();
        tab2Content.setId("Groups or Banks");
        buildGroupSearchPane(tab2Content, provider.getGroupingName());
        tab2.setContent(tab2Content);

        Tab tab3 = new Tab("Detail");
        VBox tab3Content = new VBox();
        buildBankSearchPane(tab3Content);
        tab3.setContent(tab3Content);

        tabPane.getTabs().addAll(tab1, tab2, tab3);

        getChildren().add(tabPane);

        logger.debug("built {}", getId());
    }

    private List<DynamicPane> dynamicPanes = new ArrayList<>();
    private List<ValuePane> valuePanes = new ArrayList<>();

    private void buildDynamicSearchPane(VBox pane) {
        if (! pane.getChildren().isEmpty()) {
            pane.getChildren().clear();
        }

        pane.setPadding(new Insets(5, 0, 0, 0));
        pane.setSpacing(5);

        TitledSearchPane titleSearchPane = new TitledSearchPane(this, "Patches");
        pane.getChildren().add(titleSearchPane);

        Button searchButton = titleSearchPane.getSearchButton();

        for (DynamicPane dynaPane : dynamicPanes) {
            if (dynaPane != null) {
                dynaPane.addLinkedSearchButton(searchButton);
                pane.getChildren().add(dynaPane);
            }
        }
    }

    public void addDynamicPane(int id) {
        for (int i = dynamicPanes.size() - 2; i > id; i-- ) {
            DynamicPane dynaPane = dynamicPanes.get(i);
            if (dynaPane != null) {
                dynaPane.setButtonId(i+1);
            }
            dynamicPanes.set(i+1, dynaPane);
        }

        if (id+1 < dynamicPanes.size()) {
            dynamicPanes.set(id+1, new DynamicPane(this, id+1));
        }

        getChildren().clear();
        buildPane();
    }

    public void removeDynamicPane(int id) {
        if (id > 0) {
            for (int i = id; i < dynamicPanes.size() - 1; i++) {
                DynamicPane dynaPane = dynamicPanes.get(i+1);
                if (dynaPane != null) {
                    dynaPane.setButtonId(i);
                }
                dynamicPanes.set(i, dynaPane);
            }

            dynamicPanes.set(dynamicPanes.size()-1, null);

            getChildren().clear();
            buildPane();
//            handleSearch(false);
        }
    }

    // TODO this must register for keyboard voice change
    private void buildGroupSearchPane(VBox pane, String label) {
        if (! pane.getChildren().isEmpty()) {
            pane.getChildren().clear();
        }

        pane.setPadding(new Insets(5, 0, 0, 0));
        pane.setSpacing(5);

        TitledSearchPane titleSearchPane = new TitledSearchPane(this, label);
        pane.getChildren().add(titleSearchPane);

        Button searchButton = titleSearchPane.getSearchButton();

        GroupPane groupPane = new GroupPane(this);
        groupPane.addLinkedSearchButton(searchButton);
        pane.getChildren().add(groupPane);
    }

    private void buildBankSearchPane(VBox pane) {
        if (! pane.getChildren().isEmpty()) {
            pane.getChildren().clear();
        }

        pane.setSpacing(10);

        pane.setPadding(new Insets(5, 0, 0, 0));

        TitledSearchPane titleSearchPane = new TitledSearchPane(this, "Patch Values");
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

//        setupValuePanes();
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return parent;
    }
}
