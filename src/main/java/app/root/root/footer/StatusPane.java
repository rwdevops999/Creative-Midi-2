package app.root.root.footer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.registry.AppEvent;
import util.registry.Registry;

import static util.app.colors.ColorScheme.getColor;
import static util.Util.setPaneSizeAsPercentage;

public class StatusPane extends HBox {
    private static final Logger logger = LoggerFactory.getLogger(StatusPane.class);

    public StatusPane() {
        super();

        setId("StatusPane");

        setAlignment(Pos.CENTER_LEFT);
    }

    private FooterPane parent;
    public StatusPane(FooterPane owner, int width, int height) {
        this();

        this.parent = owner;

        setPaneSizeAsPercentage(this, owner, width, height);

        buildPane();
    }

    private StringProperty dsStatus = new SimpleStringProperty("");

    private void buildPane() {
        logger.debug("Building {}", getId());

        Font font = Font.font("Arial", FontWeight.BOLD, 12);

        Text statusText = new Text();
        statusText.setFill(getColor("status"));
        statusText.setWrappingWidth(0);
        statusText.setFont(font);
        statusText.textProperty().bind(dsStatus);
        HBox.setMargin(statusText, new Insets(0, 0, 0, 5));

        getChildren().add(statusText);

        Registry.getInstance().register(event -> {
            if (event != null && AppEvent.AppEventType.STATUS.equals(event.type())) {
                String statusString = (String)event.data();
                dsStatus.set(statusString);
            }
        });

        logger.debug("Built {}", getId());
    }

    // ACCESSORS

    public FooterPane getFooterPane() {
        return parent;
    }
}
