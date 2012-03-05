package bbejeck.nio.files.directory.event;

import bbejeck.nio.files.directory.event.PathEvent;
import bbejeck.nio.files.event.PathEventContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 2:05 PM
 */
public class PathEvents implements PathEventContext {

    private final List<PathEvent> pathEvents = new ArrayList<>();
    private final Path watchedDirectory;
    private final boolean isValid;

    PathEvents(boolean valid, Path watchedDirectory) {
        isValid = valid;
        this.watchedDirectory = watchedDirectory;
    }

    @Override
    public boolean isValid(){
        return isValid;
    }

    @Override
    public Path getWatchedDirectory(){
        return watchedDirectory;
    }

    @Override
    public List<PathEvent> getEvents() {
        return Collections.unmodifiableList(pathEvents);
    }

    public void add(PathEvent pathEvent) {
        pathEvents.add(pathEvent);
    }
}
