package bbejeck.nio.files;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/10/12
 * Time: 10:18 PM
 */
import org.junit.Test;

import java.io.File;
import java.nio.file.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class PathsTest {
    
    @Test
    public void testPathsMultipleArgsOnePath() throws  Exception{
          String expected = "src/test/resources/test.txt";
          Path path = Paths.get("src/","test/","resources/","test.txt");
          assertThat(expected,is(path.toString()));
    }
    
    @Test
    public void testPathsComparedToFileSystemPath() throws Exception {
        String expected = "src/test/resources/test.txt";
        Path path = Paths.get("src/","test/","resources/","test.txt");
        Path fsPath = FileSystems.getDefault().getPath("src/","test/","resources/","test.txt");
        assertThat(Files.isSameFile(path,fsPath),is(true));
    }

    @Test
      public void testPathsMultipleArgsOnePathFirstAbsolute(){
        String expected = "/src/test/resources/test.txt";
        Path path = Paths.get("/src/","/test/","//resources////","/test.txt");
        assertThat(path.toString(),is(expected));
    }

    @Test
    public void testPathsMultipleArgsOnePathNotFirstAbsolute(){
        String expected = "src/test/resources/test.txt";
        Path path = Paths.get("src/","test/","/resources/","/test.txt");
        assertThat(expected,is(path.toString()));
    }

    @Test
    public void testRelativizeFromLongBasePath() {
        Path longParentPath = Paths.get("/usr/local/lib/foo");
        Path path1 = Paths.get("bar/baz");
        Path resolved = longParentPath.resolve(path1);
        Path relative = longParentPath.relativize(resolved);
        assertThat(resolved.toString(),is("/usr/local/lib/foo/bar/baz"));
        assertThat(relative.toString(),is("bar/baz"));
    }
    
    @Test
    public void testPathGetFileName(){
        Path path = Paths.get("src/test/resources/test.txt");
        assertThat("test.txt",is(path.getFileName().toString()));
    }
    
    @Test
    public void testResolveRelativePath() {
        Path basePath = Paths.get("/usr/local");
        String relativePath = "lib/hadoop";
        Path resolved = basePath.resolve(relativePath);
        assertThat(resolved.toString(),is("/usr/local/lib/hadoop"));
    }
    
    @Test
    public void testRelativizePathFromAbsolute() {
        Path basePath = Paths.get("/usr/local");
        Path relativePath = Paths.get("/lib/hadoop");
        Path resolved = basePath.relativize(relativePath);
        assertThat(resolved.toString(),is("../../lib/hadoop"));
    }
    
    @Test
    public void testRelativizePathFromParent() {
        Path p = Paths.get("/usr");
        Path p2 = Paths.get("/usr/local/lib");
        Path p3 = Paths.get("/Users");
        Path p4 = p.relativize(p2);
        Path p5 = p3.resolve(p.relativize(p2));
        assertThat(p5.toString(),is("/Users/local/lib"));
    }


    @Test
    public void testResolveSibling() {
        /*
           Directory structure
               /parent 
                      child1
                      child2
         */
        Path path = Paths.get("/parent/child1");
        Path sibling = Paths.get("child2");
        Path resolved = path.resolveSibling(sibling);
        assertThat(resolved.toString(),is("/parent/child2"));
    }
    
    @Test
    public void testResolveSilblingDirAndFileName(){
        Path base = Paths.get("/parent/foo");
        Path sibling = Paths.get("bar");
        Path resolvedSibling = base.resolveSibling(sibling);
        assertThat(resolvedSibling.toString(),is("/parent/bar"));
    }

    @Test
    public void testPathHasRootElement() {
        Path path = Paths.get("/Users/bbejeck");
        Path root = path.getRoot();
        assertNotNull(root);
        assertThat(root.toString(),is("/"));
    }
    
    @Test
    public void testRelativizeWithLongPath(){
        Path base = Paths.get("/usr");
        Path foo = base.resolve("foo");
        Path bar = foo.resolve("bar");
        Path baz = bar.resolve("baz");
        assertThat(baz.toString(),is("/usr/foo/bar/baz"));
        Path relative1 = base.relativize(baz);
        assertThat(relative1.toString(),is("foo/bar/baz"));
    }

    @Test
    public void testPathHasNoRootElement() {
        Path path = Paths.get("Users/bbejeck");
        Path root = path.getRoot();
        assertNull(root);
    }

    
    @Test
    public void testResolvePathAbsolute(){
        Path path = Paths.get("/Users/bbejeck");
        Path resolved = path.resolve("/dev");
        assertThat(resolved.isAbsolute(),is(true));
        assertThat(resolved.toString(),is("/dev"));
    }
    
    @Test
    public void testConvertPathToFile(){
        File file = Paths.get("/Users","bbejeck","dev").toFile();
        assertThat(file.getAbsolutePath(),is("/Users/bbejeck/dev"));
    }

    @Test
    public void testPathIsDirectory(){
        Path path = Paths.get("src/test/resources/");
        assertThat("resources",is(path.getFileName().toString()));
    }
}
