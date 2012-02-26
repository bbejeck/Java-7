package bbejeck.nio.files.directory.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/25/12
 * Time: 9:19 PM
 */

public class DirectoryEventWatcherProvider implements Provider<DirectoryEventWatcher> {
    
    private EventBus eventBus;
    private String  startPath;
    
    
    @Inject
    public DirectoryEventWatcherProvider(EventBus eventBus, @Named("START_PATH") String startPath) {
        this.eventBus = eventBus;
        this.startPath = startPath;
    }

    @Override
    public DirectoryEventWatcher get() {
        return new DirectoryEventWatcherImpl(eventBus, Paths.get(startPath));
    }
}
