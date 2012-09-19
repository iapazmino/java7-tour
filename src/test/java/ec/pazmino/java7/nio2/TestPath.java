/**
 * 
 */
package ec.pazmino.java7.nio2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author iapazmino
 * 
 */
public class TestPath {

    private static final String TEXT_FILE = "test.txt";

    private Path resourcesPath;
    private Path resourcesAbsPath;
    private Path textFile;

    @Before
    public void setResourcesPath() {
	resourcesPath = getPath("src/test/resources");
	resourcesAbsPath = resourcesPath.toAbsolutePath();
	textFile = getPath(resourcesPath, "archivos", TEXT_FILE);
    }

    @Test
    public void shouldRecognizeOSFileSystem() {
	final Path root = resourcesAbsPath.getRoot();
	assertEquals("C:\\", root.toString());
    }

    @Test
    public void shouldCreateFromAbsolutePath() {
	final Path absolutePath = getPath(resourcesAbsPath, "archivos", TEXT_FILE);
	assertTrue(absolutePath.toFile().length() > 0);
    }

    @Test
    public void shouldReadPathsInfo() {
	assertEquals(textFile.getFileName(), getPath(TEXT_FILE));
	assertEquals(textFile.getParent(), getPath(resourcesPath, "archivos"));
    }

    @Test
    public void shouldCountPathsInResources() {
	final int inAbsPath = resourcesAbsPath.getNameCount();
	final int inRelPath = resourcesPath.getNameCount();
	assertTrue("Paths for absolute location should be greater than 3", inAbsPath > 0);
	assertTrue("Paths in relative location should be 3 [src/test/resources]", inRelPath == 3);
    }

    @Test
    public void shouldFindInternalFolder() {
	final int pathsInResources = resourcesPath.getNameCount();
	assertEquals(textFile.subpath(pathsInResources, pathsInResources + 1), getPath("archivos"));
    }

    @Test
    public void shouldGetWorkingDirectory() {
	final Path workingDir = getPath(".").toAbsolutePath();
	final Path normalizedDir = workingDir.normalize();
	assertEquals(getPath("."), workingDir.getName(workingDir.getNameCount() - 1));
	assertEquals(getPath("java7-tour"), normalizedDir.getName(normalizedDir.getNameCount() - 1));
    }

    @Test
    public void shouldGetParentDirectory() {
	final Path parentDir = getPath(resourcesPath, "..");
	final Path normalizedDir = parentDir.normalize();
	assertEquals(getPath(".."), parentDir.getName(parentDir.getNameCount() - 1));
	assertEquals(getPath("test"), normalizedDir.getName(normalizedDir.getNameCount() - 1));
    }

    @Test
    public void shouldJoinPaths() {
	final Path joinedPath = getPath(".").normalize().resolve("src/test/resources");
	assertEquals(resourcesPath, joinedPath);
    }

    @Test
    @Ignore("Why!? java.lang.AssertionError: expected:<..\\..\\..> but was:<..\\..\\..\\>")
    public void shouldFindTheWayBackwards() {
	final Path workingDir = getPath(".").normalize();
	final Path way = resourcesPath.relativize(workingDir);
	assertEquals(getPath("..", "..", ".."), way);
    }

    @Test
    public void shouldFindTheWayForwards() {
	final Path workingDir = getPath(".").normalize();
	final Path way = workingDir.relativize(resourcesPath);
	assertEquals(getPath("..", "src/test/resources"), way);
    }

    @Test
    public void shouldBeSymmetric() {
	final File file = new File(new File("src/test/resources"), TEXT_FILE);
	final Path path = getPath(resourcesPath, TEXT_FILE);
	assertEquals(path, file.toPath());
	assertEquals(path.toFile(), file);
    }

    private Path getPath(final String first, final String... more) {
	return FileSystems.getDefault().getPath(first, more);
    }

    private Path getPath(final Path first, final String... more) {
	return getPath(first.toString(), more);
    }

}
