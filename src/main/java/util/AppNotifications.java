package util;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class AppNotifications {
    public static void showNotification(Object owner, String title, String message) {
        Notifications.create()
                .owner(owner)
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(3)) // Automatically hides after 5 seconds
                .position(Pos.TOP_LEFT)     // Set corner position on screen
                .showInformation();
    }

    public static void showNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(3)) // Automatically hides after 5 seconds
                .position(Pos.TOP_LEFT)     // Set corner position on screen
                .showInformation();
    }
}
