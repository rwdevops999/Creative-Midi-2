package scenes.eventlist.filter;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.EventListPane;
import scenes.eventlist.editor.EventListEditorPane;
import util.Util;

import java.io.InputStream;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterTitlePane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterTitlePane.class);

    public FilterTitlePane() {
        super();

        setId("FilterTitlePane");

        setAlignment(Pos.CENTER);
    }

    private EventListFilterPane parent;
    public FilterTitlePane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    private  void buildPane() {
        logger.debug("Building {}", getId());

        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/FenwickWoodtype.ttf");
        Font font = Font.loadFont(fontStream, 42);
        Text title = new Text("Midi Events List");
        title.setFont(font);
        setAlignment(Pos.CENTER);
        getChildren().add(title);

        logger.debug("Built {}", getId());
    }
}
