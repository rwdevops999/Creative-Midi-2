package util;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import util.app.colors.ColorScheme;

public class util {
    public static void setPaneHeightAsPercentage(Pane child, Pane parent, int size) {
        double height = size / 100.0;

        child.minHeightProperty().bind(parent.heightProperty().multiply(height));
        child.maxHeightProperty().bind(parent.heightProperty().multiply(height));
        child.prefHeightProperty().bind(parent.heightProperty().multiply(height));
    }

    public static void setPaneWidthAsPercentage(Pane child, Pane parent, int size) {
        double width = size / 100.0;

        child.minWidthProperty().bind(parent.widthProperty().multiply(width));
        child.maxWidthProperty().bind(parent.widthProperty().multiply(width));
        child.prefWidthProperty().bind(parent.widthProperty().multiply(width));
    }

    public static void setPaneSizeAsPercentage(Pane child, Pane parent, int width,int height) {
        setPaneWidthAsPercentage(child, parent, width);
        setPaneHeightAsPercentage(child, parent, height);
    }

    public static void setPaneBackground(Pane pane) {
        Background background = new Background(new BackgroundFill(ColorScheme.getColor(), null, null));
        pane.setBackground(background);
    }

    // This is for development only
    private static final int BORDER_SIZE=3;
    public static void showPaneBorder (Pane pane, Color color) {
        pane.setBorder(new Border(new BorderStroke(
                color,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(BORDER_SIZE)
        )));
    }

}
