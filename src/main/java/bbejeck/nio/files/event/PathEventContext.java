package bbejeck.nio.files.event;

import bbejeck.nio.files.directory.event.PathEvent;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 10:00 PM
 */

public interface PathEventContext {

    boolean isValid();

    Path getWatchedDirectory();

    List<PathEvent> getEvents();

}
