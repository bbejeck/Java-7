package bbejeck.nio.files.visitor;

import com.google.common.base.Predicate;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/29/12
 * Time: 10:11 PM
 */
public class CopyPredicateVisitor extends SimpleFileVisitor<Path> {

    private Path fromPath;
    private Path toPath;
    private Predicate<Path> copyPredicate;

    public CopyPredicateVisitor(Path fromPath, Path toPath, Predicate<Path> copyPredicate) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.copyPredicate = copyPredicate;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (copyPredicate.apply(dir)) {
            Path targetPath = toPath.resolve(fromPath.relativize(dir));
            if (!Files.exists(targetPath)) {
                Files.createDirectory(targetPath);
            }
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, toPath.resolve(fromPath.relativize(file)));
        return FileVisitResult.CONTINUE;
    }
}
