package bbejeck.nio.files;

import bbejeck.nio.util.DirUtils;
import bbejeck.nio.util.FileGenerator;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/31/12
 * Time: 9:31 PM
 */

public class BaseFileTest {
    protected String baseDir = "test-files";
    protected String dir1 = "dir1";
    protected String dir2 = "dir2";
    protected String fileName;
    protected String fooDir = "foo";
    protected String copyDir = "copy";
    protected String tempDir = "temp";
    protected Path basePath;
    protected Path dir1Path;
    protected Path dir2Path;
    protected Path fooPath;
    protected Path sourcePath;
    protected Path copyPath;
    protected String[] fakeJavaFiles = new String[]{"Code.java", "Foo.java", "Bar.java", "Baz.java"};
    protected String[] textFileNames = new String[]{"persons.csv", "counts.csv", "CountyTaxes.csv"};


    @Before
    public void setUp() throws Exception {
        createPaths();
        cleanUp();
        createDirectories();
        generateFile(Paths.get(baseDir, fileName), 500);
        generateFiles(basePath, fakeJavaFiles, textFileNames);
        generateFiles(dir1Path, fakeJavaFiles);
        generateFiles(dir2Path, fakeJavaFiles);
    }

    protected void createPaths() {
        basePath = Paths.get(baseDir);
        dir1Path = basePath.resolve(dir1);
        dir2Path = basePath.resolve(dir2);
        fileName = "test.txt";
        sourcePath = basePath.resolve(fileName);
        copyPath = basePath.resolve(copyDir);
        fooPath = basePath.resolve(fooDir);
    }

    protected void cleanUp() throws Exception {
        DirUtils.deleteIfExists(basePath);
        Files.deleteIfExists(basePath.resolve(Paths.get(copyDir, fooDir)));
        Files.deleteIfExists(basePath.resolve(Paths.get(copyDir, tempDir)));
        Files.deleteIfExists(basePath.resolve(tempDir));
        Files.deleteIfExists(basePath.resolve("tempII"));
    }

    protected void createDirectories() throws Exception {
        Files.createDirectories(dir1Path);
        Files.createDirectories(dir2Path);
        Files.createDirectories(copyPath);
        Files.createDirectories(fooPath);
    }

    protected void generateFile(Path path, int numberLines) throws Exception {
        FileGenerator.generate(path, numberLines);
    }

    protected void generateFiles(Path path, String[]... fileNames) throws Exception {
        for (String[] fileNamesArray : fileNames) {
            for (String fileName : fileNamesArray) {
                generateFile(path.resolve(fileName), 10);
            }
        }
    }
}
