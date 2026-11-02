package scenes.eventlist.filter;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterVoicePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterVoicePane.class);

    public FilterVoicePane() {
        super();

        setId("FilterVoicePane");
    }

    private EventListFilterPane parent;
    private FilterVoicePane pane;
    public FilterVoicePane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        pane = this;

        buildPane();
    }

    private  void buildPane() {
        Button testButton = new Button("Test");
        getChildren().add(testButton);
        testButton.setOnAction(e -> {
            double width = pane.getWidth();   // Breedte in pixels
            double height = pane.getHeight();
            System.out.println("width: " + width + " height: " + height);
        });
//        VoiceFilterPane voiceFilterPane = new VoiceFilterPane(this);
//        getChildren().add(voiceFilterPane);
    }
}
