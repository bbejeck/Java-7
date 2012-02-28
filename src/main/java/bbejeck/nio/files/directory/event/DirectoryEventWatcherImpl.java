package bbejeck.nio.files.directory.event;

import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/15/12
 * Time: 10:56 PM
 */

public class DirectoryEventWatcherImpl implements DirectoryEventWatcher {

    private FutureTask<Integer> watchTask;
    private EventBus eventBus;
    private WatchService watchService;
    private volatile boolean keepWatching = true;
    private Path startPath;


    DirectoryEventWatcherImpl(EventBus eventBus, Path startPath) {
        this.eventBus = Objects.requireNonNull(eventBus);
        this.startPath = Objects.requireNonNull(startPath);
    }

    @Override
    public void start() throws IOException {
        initWatchService();
        registerDirectories();
        createWatchTask();
        startWatching();
    }

    @Override
    public boolean isRunning() {
        return watchTask != null && !watchTask.isDone();
    }

    @Override
    public void stop() {
        keepWatching = false;
    }

   //Used for testing purposes
   Integer getEventCount() {
        try {
            return watchTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private void createWatchTask() {
        watchTask = new FutureTask<>(new Callable<Integer>() {
            private int totalEventCount;

            @Override
            public Integer call() throws Exception {
                while (keepWatching) {
                    WatchKey watchKey = watchService.poll(10, TimeUnit.SECONDS);
                    if (watchKey != null) {
                        List<WatchEvent<?>> events = watchKey.pollEvents();
                        Path watched = (Path) watchKey.watchable();
                        PathEvents pathEvents = new PathEvents(watchKey.isValid(), watched);
                        for (WatchEvent event : events) {
                            pathEvents.add(new PathEvent((Path) event.context(), event.kind()));
                            totalEventCount++;
                        }
                        watchKey.reset();
                        eventBus.post(pathEvents);
                    }
                }
                return totalEventCount;
            }
        });
    }

    private void startWatching() {
        new Thread(watchTask).start();
    }

    private void registerDirectories() throws IOException {
        Files.walkFileTree(startPath, new WatchServiceRegisteringVisitor());
    }

    private WatchService initWatchService() throws IOException {
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }
        return watchService;
    }

    private class WatchServiceRegisteringVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            return FileVisitResult.CONTINUE;
        }
    }
}
