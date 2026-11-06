package scenes.voice;

import entity.voice.Patch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

import java.util.List;

import static util.app.colors.ColorScheme.getColor;

public class VoiceListPane extends StackPane {
    private static final Logger logger = LoggerFactory.getLogger(VoiceListPane.class);

    public VoiceListPane() {
        super();

        setId("VoiceListPane");
    }

    private VoiceResultPane parent;
    public VoiceListPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "green"));

        buildPane();
    }

    private void buildPane() {
        logger.debug("building {}", getId());

        logger.debug("built {}", getId());
    }

    public void showResults (List<Patch> data) {
        System.out.println("Showing Results: " + data.size());
/*        ObservableList<Patch> tableData = FXCollections.observableArrayList(data);
        table.setItems(tableData);

        autoFitColumn(table, patchColumn); */
    }


    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
