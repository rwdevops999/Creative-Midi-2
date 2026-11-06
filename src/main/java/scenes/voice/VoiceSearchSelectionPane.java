package scenes.voice;

import entity.voice.Patch;
import fx.component.DynamicPane;
import fx.component.GroupPane;
import fx.component.TitledSearchPane;
import fx.component.ValuePane;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import util.voice.VoiceFinder;

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

    private static final int TAB_PATCH = 0;
    private static final int TAB_GROUP = 1;
    private static final int TAB_BANK = 2;

    private static int selectedTIndex = TAB_PATCH;

    private void buildPane() {
        logger.debug("building {}", getId());

        TabPane tabPane = new TabPane();
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (selectedTIndex != (int)newIdx) {
                selectedTIndex = (int)newIdx;
                handleSearch(true);
            }
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

        Platform.runLater(() -> {
            tabPane.getSelectionModel().select(selectedTIndex);
        });

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
        searchButton.setOnAction(event -> {
            handleSearch(true);
        });

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
            handleSearch(false);
        }
    }

    private GroupPane groupPane;
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
        searchButton.setOnAction(event -> {
            handleSearch(true);
        });

        groupPane = new GroupPane(this);
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
        searchButton.setOnAction(event -> {
            handleSearch(true);
        });

        ValuePane valuePane = new ValuePane(this, "Bank", bankChangedProperty.get());
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "MSB", msbChangedProperty.get());
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "LSB", lsbChangedProperty.get());
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        valuePane = new ValuePane(this, "PC", pcChangedProperty.get());
        valuePane.addLinkedSearchButton(searchButton);
        valuePanes.add(valuePane);

        for (ValuePane p : valuePanes) {
            pane.getChildren().add(p);
        }

        setupValuePanes();
    }

    private static final int BANK_PANE = 0;
    private static final int MSB_PANE = 1;
    private static final int LSB_PANE = 2;
    private static final int PC_PANE = 3;

    private StringProperty bankChangedProperty = new SimpleStringProperty("" + 0);
    private StringProperty msbChangedProperty = new SimpleStringProperty("" + 0);
    private StringProperty lsbChangedProperty = new SimpleStringProperty(""+ 0);
    private StringProperty pcChangedProperty = new SimpleStringProperty();

    private void setupValuePanes() {
        ValuePane bankPane = valuePanes.get(BANK_PANE);
        bankPane.addLinkedProperty(bankChangedProperty);

        bankChangedProperty.addListener((obs, oldValue, newValue) -> {
                int bank;
                try {
                    bank = Integer.parseInt(bankChangedProperty.get());
                } catch (NumberFormatException e) {
                    bank = 0;
                    bankChangedProperty.set(""+bank);
                }

                ValuePane msbPane = valuePanes.get(MSB_PANE);
                msbPane.setInputValue("" + (bank >> 7));

                ValuePane lsbPane = valuePanes.get(LSB_PANE);
                lsbPane.setInputValue("" + (bank & 0x7F));
        });

        ValuePane msbPane = valuePanes.get(MSB_PANE);
        msbPane.addLinkedProperty(msbChangedProperty);
        msbChangedProperty.addListener((obs, oldValue, newValue) -> {
                int msb;
                try {
                    msb = Integer.parseInt(msbChangedProperty.get());
                } catch (NumberFormatException e) {
                    msb = 0;
                    msbChangedProperty.set(""+msb);
                }
                int lsb;
                try {
                    lsb = Integer.parseInt(lsbChangedProperty.get());
                } catch (NumberFormatException e) {
                    lsb = 0;
                    lsbChangedProperty.set(""+lsb);
                }

                int bank = (msb << 7) | lsb;
                bankPane.setInputValue("" + bank);
        });

        ValuePane lsbPane = valuePanes.get(LSB_PANE);
        lsbPane.addLinkedProperty(lsbChangedProperty);
        lsbChangedProperty.addListener((obs, oldValue, newValue) -> {
                int msb;
                try {
                    msb = Integer.parseInt(msbChangedProperty.get());
                } catch (NumberFormatException e) {
                    msb = 0;
                    msbChangedProperty.set(""+msb);
                }
                int lsb;
                try {
                    lsb = Integer.parseInt(lsbChangedProperty.get());
                } catch (NumberFormatException e) {
                    lsb = 0;
                    lsbChangedProperty.set(""+lsb);
                }

                int bank = (msb << 7) | lsb;
                bankPane.setInputValue("" + bank);
        });

        ValuePane pcPane = valuePanes.get(PC_PANE);
        pcPane.addLinkedProperty(pcChangedProperty);
        pcChangedProperty.addListener((obs, oldValue, newValue) -> {
                try {
                    Integer.parseInt(pcChangedProperty.get());
                } catch (NumberFormatException e) {
                    pcChangedProperty.set(null);
                }
        });
    }

    public void handleSearch(boolean checkgroups) {
        if (selectedTIndex == TAB_PATCH) {
/*            if (checkgroups) {
                if ( getVoiceSearchPane().getGroupSearchPane().getGroupPane().getSelectedGroup() != null) {
                    getVoiceSearchPane().getGroupSearchPane().handleSearch();
                    return;
                }
            }

            getVoiceSearchPane().getGroupSearchPane().getGroupPane().setSelectedGroup(null);
*/
            String voiceToSearch = dynamicPanes.get(0).getInputValue();
            List<Patch> result = new ArrayList<>();

            if (voiceToSearch == null) {
                voiceToSearch = "";
            }

            result = VoiceFinder.findVoice(voiceToSearch);

            for (int i = 1; i < dynamicPanes.size(); i++) {
                if (dynamicPanes.get(i) != null) {
                    String voiceName = dynamicPanes.get(i).getInputValue();
                    if (voiceName != null) {
                        result = refine(result, voiceName.toLowerCase());
                    }
                }
            }

            getVoiceSearchPane().getVoicePane().getVoiceResultPane().getVoiceListPane().showResults(result);
        } else if (selectedTIndex == TAB_BANK) {
            int bank = Integer.parseInt(bankChangedProperty.get());
            int msb = Integer.parseInt(msbChangedProperty.get());
            int lsb = Integer.parseInt(lsbChangedProperty.get());
            Integer pc = null;
            if (pcChangedProperty.get() != null) {
                pc = Integer.parseInt(pcChangedProperty.get());
            }

            Patch searchPatch = new Patch();
            searchPatch.setBank(bank);
            searchPatch.setMsb(msb);
            searchPatch.setLsb(lsb);
            searchPatch.setPc(pc);

            List<Patch> result = new ArrayList<>();
            result = VoiceFinder.findVoice(searchPatch);
            getVoiceSearchPane().getVoicePane().getVoiceResultPane().getVoiceListPane().showResults(result);
        }  else if (selectedTIndex == TAB_GROUP) {
            List<Patch> result = new ArrayList<>();

            String groupName = groupPane.getSelectedGroup();

            if (groupName != null) {
                result.addAll(VoiceFinder.getVoicesOfGroup(AppInfo.INSTANCE.getInstrumentProvider().getGroups(), groupName));
            }

            getVoiceSearchPane().getVoicePane().getVoiceResultPane().getVoiceListPane().showResults(result);
        }
    }

    private List<Patch> refine(List<Patch> previousResult, String voiceName) {
        List<Patch> result = new ArrayList<>();

        for (Patch patch : previousResult) {
            if (patch.getName().toLowerCase().contains(voiceName)) {
                result.add(patch);
            }
        }

        return result;
    }

    // ACCESSORS
    public VoiceSearchPane getVoiceSearchPane() {
        return parent;
    }
}
