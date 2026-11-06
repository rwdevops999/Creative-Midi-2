package scenes.voice;

import fx.component.LightIndicatorButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;
import util.app.AppInfo;

import java.io.File;

import static util.Util.setPaneBackground;
import static util.app.colors.ColorScheme.getColor;

public class VoicePlayPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(VoicePlayPane.class);

    public VoicePlayPane() {
        super();

        setId("VoicePlayPane");

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);

        setPadding(new Insets(5));
    }

    private VoiceResultPane parent;
    public VoicePlayPane(VoiceResultPane owner, double height) {
        this();

        this.parent = owner;

        Util.setPaneHeightAsPercentage(this, owner, height);
        Util.showSmallPaneBorder(this, getColor("border", "blue"));

        setPaneBackground(this);

        buildPane();
    }

    private StringProperty dsFilename = new SimpleStringProperty("<No file loaded>");
    private File selectedFile = null;

    private void buildPane() {
        logger.debug("building {}", getId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a MIDI file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MIDI", "*.mid")
        );

        LightIndicatorButton play = new LightIndicatorButton("Play", 40, "red");
        getChildren().add(play);

        Button load = new Button("Load...");
        load.setOnAction(e -> {
            if (AppInfo.INSTANCE.getLastVisitedFolder() != null) {
                fileChooser.setInitialDirectory(new File(AppInfo.INSTANCE.getLastVisitedFolder()));
            }
            selectedFile = fileChooser.showOpenDialog(AppInfo.INSTANCE.getPrimaryStage());

            if (selectedFile != null) {
                String lastPath = FilenameUtils.getFullPath(selectedFile.getPath());
                AppInfo.INSTANCE.setLastVisitedFolder(lastPath);

                dsFilename.set(FilenameUtils.removeExtension(selectedFile.getName()));
            }
        });
        getChildren().add(load);

        Label fileName = new Label();
        fileName.textProperty().bindBidirectional(dsFilename);

        fileName.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(fileName);

        logger.debug("built {}", getId());
    }

    // ACCESSORS
    public VoiceResultPane getVoiceResultPane() {
        return parent;
    }
}
