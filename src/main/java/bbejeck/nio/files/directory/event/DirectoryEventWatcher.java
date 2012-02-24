package bbejeck.nio.files.directory.event;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/15/12
 * Time: 10:29 PM
 */

public interface DirectoryEventWatcher {
    void start() throws IOException;

    boolean isRunning();

    void stop();
}
