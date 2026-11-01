package util.registry;

public record AppEvent(AppEventType  type, Object data) {
    public enum AppEventType {
        DEVICE,
        STATUS,
        MONITOR
    }
}
