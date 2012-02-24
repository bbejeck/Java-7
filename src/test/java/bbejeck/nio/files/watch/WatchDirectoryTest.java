package bbejeck.nio.files.watch;

import bbejeck.nio.files.BaseFileTest;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/13/12
 * Time: 9:47 PM
 */

public class WatchDirectoryTest extends BaseFileTest {
    private WatchService watchService;
    private WatchKey basePathWatchKey;

    @Before
    public void setUo() throws Exception {
        super.setUp();
        watchService = FileSystems.getDefault().newWatchService();
        basePathWatchKey = basePath.register(watchService,ENTRY_CREATE);
    }

    @Test
    public void testEventForDirectory() throws Exception {
        generateFile(basePath.resolve("newTextFile.txt"), 10);
        generateFile(basePath.resolve("newTextFileII.txt"), 10);
        generateFile(basePath.resolve("newTextFileIII.txt"), 10);
        WatchKey watchKey = watchService.poll(20, TimeUnit.SECONDS);
        assertNotNull(watchKey);
        assertThat(watchKey,is(basePathWatchKey));
        List<WatchEvent<?>> eventList = watchKey.pollEvents();
        assertThat(eventList.size(), is(3));
        for (WatchEvent event : eventList) {
            assertThat(event.kind() == StandardWatchEventKinds.ENTRY_CREATE, is(true));
            assertThat(event.count(),is(1));
        }
        Path eventPath = (Path) eventList.get(0).context();
        assertThat(Files.isSameFile(eventPath, Paths.get("newTextFile.txt")), is(true));
        Path watchedPath = (Path) watchKey.watchable();
        assertThat(Files.isSameFile(watchedPath, basePath), is(true));
    }

    @Test
    public void testEventForDirectoryWatchKey() throws Exception {
        generateFile(basePath.resolve("newTextFile.txt"), 10);
        List<WatchEvent<?>> eventList = basePathWatchKey.pollEvents();
        while (eventList.size() == 0 ){
            eventList = basePathWatchKey.pollEvents();
            Thread.sleep(10000);
        }
        assertThat(eventList.size(), is(1));
        for (WatchEvent event : eventList) {
            assertThat(event.kind() == StandardWatchEventKinds.ENTRY_CREATE, is(true));
        }
        basePathWatchKey.reset();
        generateFile(basePath.resolve("newTextFileII.txt"), 10);
        generateFile(basePath.resolve("newTextFileIII.txt"), 10);
        while (eventList.size() == 0 ){
            eventList = basePathWatchKey.pollEvents();
            Thread.sleep(10000);
        }
        Path eventPath = (Path) eventList.get(0).context();
        assertThat(Files.isSameFile(eventPath, Paths.get("newTextFile.txt")), is(true));
        Path watchedPath = (Path) basePathWatchKey.watchable();
        assertThat(Files.isSameFile(watchedPath, basePath), is(true));
    }



    @Test
    public void testEventForSubDirectory() throws Exception {
        dir1Path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        generateFile(basePath.resolve("newTextFile.txt"), 10);
        generateFile(dir1Path.resolve("newTextFile.txt"), 10);
        int count = 0;
        while (count < 2) {
            WatchKey watchKey = watchService.poll(20, TimeUnit.SECONDS);
            Path watchedPath = (Path) watchKey.watchable();
            assertNotNull(watchKey);
            List<WatchEvent<?>> eventList = watchKey.pollEvents();
            WatchEvent event = eventList.get(0);
            assertThat(event.count(), is(1));
            assertThat(event.kind() == StandardWatchEventKinds.ENTRY_CREATE, is(true));
            assertTrue(Files.isSameFile((Path) event.context(), Paths.get("newTextFile.txt")));
            count++;
        }
    }


}
