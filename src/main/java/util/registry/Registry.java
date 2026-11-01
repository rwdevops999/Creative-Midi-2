package util.registry;

import javafx.application.Platform;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Registry {
    private static final Logger logger = LoggerFactory.getLogger(Registry.class);

    @Getter
    private static final Registry instance = new Registry();

    private final List<AppEventListener> listeners = new CopyOnWriteArrayList<>();

    private final BlockingQueue<AppEvent> eventQueue = new LinkedBlockingQueue<>();
    private Registry() {
        startQueueProcessor();
    }

    public void register(AppEventListener listener) {
        listeners.add(listener);
    }

    public void unregister(AppEventListener listener) {
        listeners.remove(listener);
    }

    public void post(AppEvent event) {
        eventQueue.offer(event);
    }

    private void startQueueProcessor() {
        Thread dispatchThread = new Thread(() -> {
            try {
                while (! Thread.currentThread().isInterrupted()) {
                    AppEvent event = eventQueue.take();

                    Platform.runLater(() -> {
                        for (AppEventListener listener : listeners) {
                            listener.onAppEvent(event);
                        }
                    });
                }
            } catch (InterruptedException e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        dispatchThread.setDaemon(true);
        dispatchThread.start();
    }

    // specific functions
    public void showStatus(String value) {
        eventQueue.offer(new AppEvent(AppEvent.AppEventType.STATUS, value));
    }
}
