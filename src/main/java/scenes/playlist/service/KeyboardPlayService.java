package scenes.playlist.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardPlayService extends Service<Void> {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardPlayService.class);

    @Override
    protected Task<Void> createTask() {
        return null;
    }
}
