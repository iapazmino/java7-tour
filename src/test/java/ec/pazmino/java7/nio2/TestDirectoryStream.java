/**
 * 
 */
package ec.pazmino.java7.nio2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author iapazmino
 * 
 */
public class TestDirectoryStream {

    private Path archivosPath;

    @Before
    public void setResourcesPath() {
	archivosPath = getPath("src/test/resources", "archivos");
    }

    @Test(expected = PatternSyntaxException.class)
    public void shouldIdentifyBadSyntax() {
	try (DirectoryStream<Path> stream = Files.newDirectoryStream(archivosPath, "´.{")) {
	    fail("Shouldn't reach this block");
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    public void shouldFindTxtFileInDirectory() {
	try (DirectoryStream<Path> stream = Files.newDirectoryStream(archivosPath, "*.txt")) {
	    final List<Path> paths = readPaths(stream);
	    assertSize(1, paths.size());
	    assertContains(paths, archivosPath, "test.txt");
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    public void shouldFindTestFiles() {
	try (DirectoryStream<Path> stream = Files.newDirectoryStream(archivosPath, "{test}.*")) {
	    final List<Path> paths = readPaths(stream);
	    assertSize(2, paths.size());
	    assertContains(paths, archivosPath, "test.txt", "test.zip");
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    public void shouldIncludeNestedFiles() {
	try {
	    final TextFileVisitor textFileVisitor = new TextFileVisitor();
	    Files.walkFileTree(archivosPath, textFileVisitor);
	    final List<Path> paths = textFileVisitor.getFound();
	    assertSize(2, paths.size());
	    assertContains(paths, archivosPath, "test.txt", "nested/insider.txt");
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    private void assertSize(final int expected, final int actual) {
	final String error = "The list should have <%s> path(s) instead of <%s> path(s)";
	assertTrue(String.format(error, expected, actual), expected == actual);
    }

    private void assertContains(final List<Path> actual, final Path dir, final String... expected) {
	final String error = "The list should contain <%s> but it doesn't";
	Path path = null;
	for (int i = 0; i < expected.length; i++) {
	    path = getPath(dir, expected[i]);
	    assertTrue(String.format(error, path), actual.contains(path));
	}
    }

    private Path getPath(final String first, final String... more) {
	return FileSystems.getDefault().getPath(first, more);
    }

    private Path getPath(final Path first, final String... more) {
	return getPath(first.toString(), more);
    }

    private List<Path> readPaths(final Iterable<Path> stream) {
	final List<Path> paths = new ArrayList<>();
	for (Path path : stream) {
	    paths.add(path);
	}
	return paths;
    }

    private class TextFileVisitor extends SimpleFileVisitor<Path> {

	private List<Path> found = new ArrayList<>();

	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
	    // Notice the conversion to String!!!
	    if (file.toString().endsWith(".txt")) {
		found.add(file);
	    }
	    return FileVisitResult.CONTINUE;
	}

	public List<Path> getFound() {
	    return found;
	}

    }

}
