package bbejeck.nio.util;

import bbejeck.nio.files.directory.AsynchronousRecursiveDirectoryStream;
import bbejeck.nio.files.visitor.*;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.io.IOException;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/22/12
 * Time: 12:46 PM
 */

public class DirUtils {

    private DirUtils() {
    }

    /**
     * Walks file tree starting at the given path and deletes all files
     * but leaves the directory structure intact.
     *
     * @param path Base path to start from
     * @throws IOException
     */
    public static void clean(Path path) throws IOException {
        validate(path);
        Files.walkFileTree(path, new CleanDirVisitor());
    }

    /**
     * Walks file tree starting at the given path and deletes all files
     * but leaves the directory structure intact. If the given Path does not exist nothing
     * is done.
     *
     * @param path
     * @throws IOException
     */
    public static void cleanIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            validate(path);
            Files.walkFileTree(path, new CleanDirVisitor());
        }
    }


    /**
     * Completely removes given file tree starting at and including the given path.
     *
     * @param path
     * @throws IOException
     */
    public static void delete(Path path) throws IOException {
        validate(path);
        Files.walkFileTree(path, new DeleteDirVisitor());
    }


    /**
     * If the path exists, completely removes given file tree starting at and including the given path.
     *
     * @param path
     * @throws IOException
     */
    public static void deleteIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            validate(path);
            Files.walkFileTree(path, new DeleteDirVisitor());
        }
    }

    /**
     * Copies a directory tree
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copy(Path from, Path to) throws IOException {
        validate(from);
        Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS),Integer.MAX_VALUE,new CopyDirVisitor(from, to));
    }

    /**
     * Moves one directory tree to another.  Not a true move operation in that the
     * directory tree is copied, then the original directory tree is deleted.
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void move(Path from, Path to) throws IOException {
        validate(from);
        Files.walkFileTree(from, new CopyDirVisitor(from, to));
        Files.walkFileTree(from, new DeleteDirVisitor());
    }

    /**
     * Traverses the directory structure and applies the given function to each file
     * @param target
     * @param function
     * @throws IOException
     */
    public static void apply(Path target, Function<Path,FileVisitResult> function) throws IOException{
        validate(target);
        Files.walkFileTree(target,new FunctionVisitor(function));
    }

    /**
     * Traverses the directory structure and will only copy sub-tree structures where the provided predicate is true
     * @param from
     * @param to
     * @param predicate
     * @throws IOException
     */
    
    public static void copyWithPredicate(Path from, Path to, Predicate<Path> predicate) throws IOException{
        validate(from);
        Files.walkFileTree(from, new CopyPredicateVisitor(from,to,predicate));
    }

    /**
     * Returns a DirectoryStream that can iterate over files found recursively based on the pattern provided
     * @param startPath  the Directory to start from
     * @param pattern    the glob to match against files
     * @return   DirectoryStream
     * @throws IOException
     */
    public static DirectoryStream<Path> glob(Path startPath, String pattern) throws IOException{
        validate(startPath);
            return new AsynchronousRecursiveDirectoryStream(startPath,pattern);
    }
    


    private static void validate(Path... paths) {
        for (Path path : paths) {
            Objects.requireNonNull(path);
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException(String.format("%s is not a directory", path.toString()));
            }
        }
    }


}
