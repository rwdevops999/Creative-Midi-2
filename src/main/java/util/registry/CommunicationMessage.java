package util.registry;

import entity.monitor.ColoredItem;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.communication.CommunicationType;

public class CommunicationMessage {
    private static final Logger logger = LoggerFactory.getLogger(CommunicationMessage.class);

    @Getter
    private final static CommunicationMessage instance = new CommunicationMessage();

    private CommunicationMessage() {
    }

    public void showMessage(CommunicationType type, String message) {
        ColoredItem item = new ColoredItem(message, type);
        AppEvent event = new AppEvent(AppEvent.AppEventType.MONITOR, item);
        Registry.getInstance().post(event);
    }
}
