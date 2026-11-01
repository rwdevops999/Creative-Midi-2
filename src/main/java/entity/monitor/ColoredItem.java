package entity.monitor;

import entity.NameEntity;
import javafx.scene.paint.Color;
import lombok.Getter;
import util.communication.CommunicationType;

import static util.app.colors.ColorScheme.getColor;

public class ColoredItem extends NameEntity {
    private CommunicationType type;
    @Getter
    private Color messageColor;

    public ColoredItem() {
        super();
   }

   public ColoredItem(String text, CommunicationType type) {
        this.name = text;
        this.type = type;

        this.messageColor = getColor("communication", "unknown");

        this.messageColor = switch(type) {
            case INBOUND -> getColor("communication", "inbound");
            case OUTBOUND -> getColor("communication", "outbound");
            case ERROR -> getColor("communication", "error");
            case WARNING -> getColor("communication", "warning");
            case INFO -> getColor("communication", "info");
        };
   }
}
