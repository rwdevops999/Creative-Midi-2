package util;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class AppNotifications {
    public static void showNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(3)) // Automatically hides after 5 seconds
                .position(Pos.TOP_LEFT)     // Set corner position on screen
                .showInformation();
    }

}
