package bbejeck.nio.files.event;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 12:44 PM
 */

public class PathEvent {
    private Path eventTarget;
    private WatchEvent.Kind type;

    public PathEvent(Path eventTarget, WatchEvent.Kind type) {
        this.eventTarget = eventTarget;
        this.type = type;
    }

    public Path getEventTarget() {
        return eventTarget;
    }

    public WatchEvent.Kind getType() {
        return type;
    }
}
