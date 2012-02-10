package bbejeck.nio.files;

import bbejeck.nio.files.directory.AsynchronousRecursiveDirectoryStream;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/31/12
 * Time: 9:18 PM
 */
public class DirectoryStreamTest extends BaseFileTest {
    private  int expectedDirectoryCount;
    private  int expectedFileCount;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        expectedDirectoryCount = 4;
        expectedFileCount = fakeJavaFiles.length + textFileNames.length + 1;
    }

    @Test
    public void testDirectoryStream() throws Exception {
        int directoryCount = 0;
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(basePath)) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directoryCount++;
                } else if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(expectedDirectoryCount, is(directoryCount));
        assertThat(expectedFileCount, is(fileCount));
    }

    @Test
    public void testDirectoryStreamGlobAll() throws Exception {
        int directoryCount = 0;
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(basePath, "*")) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directoryCount++;
                } else if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(expectedDirectoryCount, is(directoryCount));
        assertThat(expectedFileCount, is(fileCount));
    }

    @Test
    public void testDirectoryStreamGlobPattern() throws Exception {
        int directoryCount = 0;
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(basePath, "Co*")) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directoryCount++;
                } else if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(directoryCount, is(0));
        assertThat(fileCount, is(2));
    }

    @Test
    public void testDirectoryStreamGlobRegexPattern() throws Exception {
        int directoryCount = 0;
        int fileCount = 0;
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("regex:.*/[^ C d f t].*\\.\\w{3,4}");
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return pathMatcher.matches(entry);
            }
        };
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(basePath, filter)) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directoryCount++;
                } else if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(directoryCount, is(0));
        assertThat(fileCount, is(5));
    }

    @Test
    public void testDirectoryStreamGlobMultipleFileExtensions() throws Exception {
        int directoryCount = 0;
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(basePath, "*.{java,txt}")) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directoryCount++;
                } else if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(directoryCount, is(0));
        assertThat(fileCount, is(5));
    }

}
