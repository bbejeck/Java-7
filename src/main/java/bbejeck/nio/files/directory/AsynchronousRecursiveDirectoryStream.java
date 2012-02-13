package bbejeck.nio.files.directory;

import bbejeck.nio.files.visitor.FunctionVisitor;
import bbejeck.nio.util.FilterBuilder;
import com.google.common.base.Function;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/5/12
 * Time: 1:05 PM
 */
public class AsynchronousRecursiveDirectoryStream implements DirectoryStream<Path> {

    private LinkedBlockingQueue<Path> pathsBlockingQueue = new LinkedBlockingQueue<>();
    private boolean closed = false;
    private FutureTask<Void> pathTask;
    private Path startPath;
    private Filter filter;

    public AsynchronousRecursiveDirectoryStream(Path startPath, String pattern) throws IOException {
        this.filter = FilterBuilder.buildGlobFilter(Objects.requireNonNull(pattern));
        this.startPath = Objects.requireNonNull(startPath);
    }

    @Override
    public Iterator<Path> iterator() {
        confirmNotClosed();
        findFiles(startPath, filter);
        return new Iterator<Path>() {
            Path path;
            @Override
            public boolean hasNext() {
                try {
                    path = pathsBlockingQueue.poll();
                    while (!pathTask.isDone() && path == null) {
                        path = pathsBlockingQueue.poll(5, TimeUnit.MILLISECONDS);
                    }
                    return (path != null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return false;
            }

            @Override
            public Path next() {
                return path;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removal not supported");
            }
        };
    }

    private void findFiles(final Path startPath, final Filter filter) {
        pathTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Files.walkFileTree(startPath, new FunctionVisitor(getFunction(filter)));
                return null;
            }
        });
        start(pathTask);
    }

    private Function<Path, FileVisitResult> getFunction(final Filter<Path> filter) {
        return new Function<Path, FileVisitResult>() {
            @Override
            public FileVisitResult apply(Path input) {
                try {
                    if (filter.accept(input.getFileName())) {
                        pathsBlockingQueue.offer(input);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                return (pathTask.isCancelled()) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
            }
        };
    }


    @Override
    public void close() throws IOException {
        if(pathTask !=null){
            pathTask.cancel(true);
        }
        pathsBlockingQueue.clear();
        pathsBlockingQueue = null;
        pathTask = null;
        filter = null;
        closed = true;
    }

    private void start(FutureTask<Void> futureTask) {
        new Thread(futureTask).start();
    }

    private void confirmNotClosed() {
        if (closed) {
            throw new IllegalStateException("DirectoryStream has already been closed");
        }
    }

}
