package bbejeck.nio.files.directory.event;

import bbejeck.nio.files.BaseFileTest;
import bbejeck.nio.files.event.PathEventContext;
import bbejeck.nio.files.event.PathEventSubscriber;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 10:32 PM
 */
//TODO add Test with Phaser to test adding deleting and updating
//TODO add directory on the fly then start watching and add file confirm added to new dir
public class DirectoryEventWatcherImplTest extends BaseFileTest {

    private EventBus eventBus;
    private DirectoryEventWatcherImpl dirWatcher;
    private CountDownLatch doneSignal;
    private TestSubscriber subscriber;

    @Before
    public void setUp() throws Exception {
        createPaths();
        cleanUp();
        createDirectories();
        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, basePath);
        dirWatcher.start();
        subscriber = new TestSubscriber();
        eventBus.register(subscriber);
        doneSignal = new CountDownLatch(1);
    }

    @Test
    public void testDirectoryForWrittenEvents() throws Exception {
        generateFile(basePath.resolve("newTextFile.txt"), 10);
        generateFile(basePath.resolve("newTextFileII.txt"), 10);
        generateFile(basePath.resolve("newTextFileIII.txt"), 10);
        doneSignal.await();
        assertThat(subscriber.getPathEvents().size(), is(1));
        PathEventContext event = subscriber.getPathEvents().get(0);
        assertThat(event.getWatchedDirectory(), is(basePath));
        assertThat(event.getEvents().size(), is(3));
        assertThat(dirWatcher.isRunning(), is(true));
    }


    @After
    public void tearDown() throws Exception {
        dirWatcher.stop();
    }


    private class TestSubscriber implements PathEventSubscriber {
        List<PathEventContext> pathEvents = new ArrayList<>();

        @Override
        @Subscribe
        public void handlePathEvents(PathEventContext pathEventContext) {
            pathEvents.add(pathEventContext);
            doneSignal.countDown();
        }

        public List<PathEventContext> getPathEvents() {
            return pathEvents;
        }
    }
}
