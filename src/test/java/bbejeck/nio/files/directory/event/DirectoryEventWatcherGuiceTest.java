package bbejeck.nio.files.directory.event;

import bbejeck.nio.files.BaseFileTest;
import bbejeck.nio.files.event.PathEventContext;
import bbejeck.nio.files.event.PathEventSubscriber;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/25/12
 */
public class DirectoryEventWatcherGuiceTest extends BaseFileTest {

    private DirectoryEventWatcher directoryEventWatcher;
    private CountDownLatch latch;
    private TestSubscriber subscriber;

    @Before
    public void setUp() throws Exception {
        createPaths();
        cleanUp();
        createDirectories();
        latch = new CountDownLatch(3);
        Injector injector = Guice.createInjector(new DirectoryEventModule());
        directoryEventWatcher = injector.getInstance(DirectoryEventWatcher.class);
        EventBus eventBus = injector.getInstance(EventBus.class);
        subscriber = new TestSubscriber();
        eventBus.register(subscriber);
        directoryEventWatcher.start();
    }

    @Test
    public void testDirectoryForWrittenEvents() throws Exception {
        Map<Path, String> eventResultsMap = new HashMap<>();
        assertThat(directoryEventWatcher.isRunning(), is(true));
        generateFile(dir1Path.resolve("newTextFile.txt"), 10);
        generateFile(basePath.resolve("newTextFileII.txt"), 10);
        generateFile(dir2Path.resolve("newTextFileIII.txt"), 10);
        latch.await();

        assertThat(subscriber.pathEvents.size(), is(3));
        List<PathEventContext> eventContexts = subscriber.pathEvents;

        for (PathEventContext eventContext : eventContexts) {
            Path dir = eventContext.getWatchedDirectory();
            assertThat(eventContext.getEvents().size(), is(1));
            Path target = eventContext.getEvents().get(0).getEventTarget();
            eventResultsMap.put(dir, target.toString());
        }

        Set<Path> watchedDirs = eventResultsMap.keySet();
        assertThat(watchedDirs.size(), is(3));
        assertThat(watchedDirs.contains(dir1Path), is(true));
        assertThat(watchedDirs.contains(basePath), is(true));
        assertThat(watchedDirs.contains(dir2Path), is(true));

        assertThat(eventResultsMap.get(dir1Path), is("newTextFile.txt"));
        assertThat(eventResultsMap.get(basePath), is("newTextFileII.txt"));
        assertThat(eventResultsMap.get(dir2Path), is("newTextFileIII.txt"));

    }

    @After
    public void tearDown() {
        directoryEventWatcher.stop();
    }


    private class TestSubscriber implements PathEventSubscriber {
        List<PathEventContext> pathEvents = new ArrayList<>();

        @Override
        @Subscribe
        public void handlePathEvents(PathEventContext pathEventContext) {
            pathEvents.add(pathEventContext);
            latch.countDown();
        }
    }

}
