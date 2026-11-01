package util.registry;

import lombok.Getter;
import lombok.Setter;

public record AppEvent(AppEventType  type, Object data) {
    public enum AppEventType {
        DEVICE,
        STATUS;
    }
}
