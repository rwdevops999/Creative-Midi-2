package scenes.eventlist.filter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.eventlist.filter.subpanes.ChannelFilterPane;
import util.app.AppInfo;

import static util.Util.setPaneHeightAsPercentage;
import static util.Util.showSmallPaneBorder;
import static util.app.colors.ColorScheme.getColor;

public class FilterChannelPane extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(FilterChannelPane.class);

    public FilterChannelPane() {
        super();

        setId("FilterChannelPane");
    }

    private EventListFilterPane parent;
    public FilterChannelPane(EventListFilterPane owner) {
        this();

        this.parent = owner;

        buildPane();
    }

    ChannelFilterPane channelFilterPane;

    private IntegerProperty dsMask = new SimpleIntegerProperty(0);

    private  void buildPane() {
        channelFilterPane = new ChannelFilterPane(this, dsMask);
        dsMask.addListener((observable, oldValue, newValue) -> {
            getEventListFilterPane().getEventListPane().getEventListEditorPane().getFileEditorPane().setChannelFilter(newValue.intValue());
            if (AppInfo.INSTANCE.isDebugMode()) {
                 printBits(dsMask.get());
            }
        });

        getChildren().add(channelFilterPane);
    }

    private void printBits(int value) {
        String bits = String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');

        StringBuilder resultaat = new StringBuilder();
        for (int i = 0; i < bits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                resultaat.append(" ");
            }
            resultaat.append(bits.charAt(i));
        }

        System.out.println("BITS: " + resultaat.toString());
    }

    // ACCESSOR
    public EventListFilterPane getEventListFilterPane() {
        return this.parent;
    }

    public ChannelFilterPane getChannelFilterPane() {
        return channelFilterPane;
    }
}
