package bbejeck.nio.files.directory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/12/12
 * Time: 9:59 PM
 */
public class FileDirectoryStream {
    File startDirectory;
    String pattern;
    private LinkedBlockingQueue<File> fileLinkedBlockingQueue = new LinkedBlockingQueue<>();
    private boolean closed = false;
    private FutureTask<Void> fileTask;
    private FilenameFilter filenameFilter;


    public FileDirectoryStream(String pattern, File startDirectory) {
        this.pattern = pattern;
        this.startDirectory = startDirectory;
        this.filenameFilter = getFileNameFilter(pattern);
    }

    public Iterator<File> glob() throws IOException {
        confirmNotClosed();
        startFileSearch(startDirectory, filenameFilter);
        return new Iterator<File>() {
            File file = null;

            @Override
            public boolean hasNext() {
                try {
                    file = fileLinkedBlockingQueue.poll();
                    while (!fileTask.isDone() && file == null) {
                        file = fileLinkedBlockingQueue.poll(5, TimeUnit.MILLISECONDS);
                    }
                    return file != null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return false;
            }

            @Override
            public File next() {
                return file;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    private void startFileSearch(final File startDirectory, final FilenameFilter filenameFilter) {
        fileTask = new FutureTask<Void>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                findFiles(startDirectory, filenameFilter);
                return null;
            }
        });
        start(fileTask);
    }

    private void findFiles(final File startDirectory, final FilenameFilter filenameFilter) {
        File[] files = startDirectory.listFiles(filenameFilter);
        for (File file : files) {
            if (!fileTask.isCancelled()) {
                if (file.isDirectory()) {
                    findFiles(file, filenameFilter);
                }
                fileLinkedBlockingQueue.offer(file);
            }
        }
    }

    private FilenameFilter getFileNameFilter(final String pattern) {
        return new FilenameFilter() {
            Pattern regexPattern = Pattern.compile(pattern);

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory() || regexPattern.matcher(name).matches();
            }
        };
    }


    public void close() throws IOException {
        if (fileTask != null) {
            fileTask.cancel(true);
        }
        fileLinkedBlockingQueue.clear();
        fileLinkedBlockingQueue = null;
        fileTask = null;
        closed = true;
    }

    private void start(FutureTask<Void> futureTask) {
        new Thread(futureTask).start();
    }

    private void confirmNotClosed() {
        if (closed) {
            throw new IllegalStateException("File Iterator has already been closed");
        }
    }


}
