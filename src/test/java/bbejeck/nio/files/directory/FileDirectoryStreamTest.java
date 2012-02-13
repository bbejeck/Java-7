package bbejeck.nio.files.directory;

import bbejeck.nio.files.BaseFileTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/12/12
 * Time: 10:38 PM
 */


public class FileDirectoryStreamTest extends BaseFileTest {
    private int expectedJavaFileCount;
    private int expectedTotalFileCount;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        expectedJavaFileCount = fakeJavaFiles.length * 3;
        expectedTotalFileCount = expectedJavaFileCount + textFileNames.length + 1;
    }

    @Test
    public void testGlob() throws Exception {
        File baseDir = basePath.toFile();
        FileDirectoryStream directoryStream = new FileDirectoryStream(".*", baseDir);
        Iterator<File> fileIterator = directoryStream.glob();
        int fileCount = 0;
        while (fileIterator.hasNext()) {
            File f = fileIterator.next();
            if (f.isFile()) {
                fileCount++;
            }
        }
        directoryStream.close();
        assertThat(fileCount, is(expectedTotalFileCount));
    }

    @Test
    public void testGlobJavaFiles() throws Exception {
        File baseDir = basePath.toFile();
        FileDirectoryStream directoryStream = new FileDirectoryStream(".*\\.java$", baseDir);
        Iterator<File> fileIterator = directoryStream.glob();
        int fileCount = 0;
        while (fileIterator.hasNext()) {
            File f = fileIterator.next();
            if (f.isFile()) {
                fileCount++;
            }
        }
        directoryStream.close();
        assertThat(fileCount, is(expectedJavaFileCount));
    }
}
