package bbejeck.nio.files.event;

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

    private List<PathEvent> pathEvents = new ArrayList<>();
    private Path watchedDirectory;
    private boolean isValid;

    public PathEvents(boolean valid, Path watchedDirectory) {
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
