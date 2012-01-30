package bbejeck.nio.files;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/10/12
 * Time: 11:07 PM
 */

import bbejeck.nio.util.DirUtils;
import bbejeck.nio.util.FileGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilesCopyMoveTest {

    protected String baseDir = "test-files";
    protected String dir1 = "dir1";
    protected String dir2 = "dir2";
    protected String fileName;
    protected String fooDir = "foo";
    private String copyDir = "copy";
    private String tempDir = "temp";

    protected Path basePath;
    protected Path dir1Path;
    protected Path dir2Path;
    private Path fooPath;
    private Path sourcePath;
    private Path copyPath;


    @Before
    public void setUp() throws Exception {
        basePath = Paths.get(baseDir);
        dir1Path = basePath.resolve(dir1);
        dir2Path = basePath.resolve(dir2);
        fileName = "test.txt";
        sourcePath = basePath.resolve(fileName);
        copyPath = basePath.resolve(copyDir);
        fooPath = basePath.resolve(fooDir);
        cleanUp();
        createDirectories();
        generateFiles(Paths.get(baseDir, fileName), 500);
    }


    @Test
    public void testCopyFile() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.copy(sourcePath,targetPath);
        assertThat(Files.size(targetPath), is(Files.size(sourcePath)));
        assertThat(Files.exists(sourcePath), is(true));
        assertThat(Files.exists(targetPath), is(true));
    }

    @Test
    public void testMoveFile() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.move(sourcePath, targetPath);
        assertThat(Files.exists(sourcePath), is(false));
        assertThat(Files.exists(targetPath), is(true));
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testMoveFileNoFollowLinksInvalid() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.move(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
    }

    
    @Test (expected = UnsupportedOperationException.class)
    public void testCopyInvalidOption() throws Exception{
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.copy(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    }


    @Test
    public void testCopyDirectory() throws Exception {
        Path target = basePath.resolve(copyDir);
        Path targetDir = target.resolve(basePath.relativize(fooPath));
        Files.copy(fooPath, targetDir);
        Path expectedPath = Paths.get(baseDir, copyDir, fooDir);
        assertThat(Files.exists(expectedPath), is(true));
    }

    @Test
    public void testMoveDirectory() throws Exception {
        Path tempPath = basePath.resolve(tempDir);
        Files.createDirectory(tempPath);
        Path target = basePath.resolve(Paths.get(copyDir));
        Path targetDir = target.resolve(basePath.relativize(tempPath));
        Files.move(tempPath, targetDir);
        assertThat(Files.notExists(tempPath), is(true));
        assertThat(Files.exists(basePath.resolve(Paths.get(copyDir, tempDir))), is(true));
    }

    private void cleanUp() throws Exception {
        DirUtils.deleteIfExists(basePath);
        Files.deleteIfExists(basePath.resolve(Paths.get(copyDir, fooDir)));
        Files.deleteIfExists(basePath.resolve(Paths.get(copyDir, tempDir)));
        Files.deleteIfExists(basePath.resolve(tempDir));
        Files.deleteIfExists(basePath.resolve("tempII"));
    }

    private void createDirectories() throws Exception {
        Files.createDirectories(dir1Path);
        Files.createDirectories(dir2Path);
        Files.createDirectories(copyPath);
        Files.createDirectories(fooPath);
    }

    private void generateFiles(Path path, int numberLines) throws Exception {
        FileGenerator.generate(path, numberLines);
    }

}
