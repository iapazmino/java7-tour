/**
 * 
 */
package ec.pazmino.java7.nio2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author iapazmino
 * 
 */
public class TestFiles {

    private boolean isNotPosixFileSystem;
    private Path targetDir;
    private Path source;

    @Before
    public void setupTargetDir() throws IOException {
	isNotPosixFileSystem = isNotPosixFileSystem();
	targetDir = getPath("target");
	source = getPath(targetDir, "new-file.txt");
	if (Files.exists(source)) {
	    Files.delete(source);
	}
    }

    @Test
    public void shouldCreateFile() {
	try {
	    final Path file = Files.createFile(source);
	    assertTrue("The file wasn't created", file.toFile().exists());
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    public void shouldSetAttributes() {
	if (isNotPosixFileSystem) {
	    return;
	}
	try {
	    final Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
	    final FileAttribute<Set<PosixFilePermission>> attrbs = PosixFilePermissions.asFileAttribute(perms);
	    final Path file = Files.createFile(source, attrbs);
	    final Set<PosixFilePermission> permsRead = Files.getPosixFilePermissions(file);
	    assertEquals(perms, permsRead);
	} catch (IOException e) {
	    fail(e.getMessage());
	}
    }

    private Path getPath(final String first, final String... more) {
	return FileSystems.getDefault().getPath(first, more);
    }

    private Path getPath(final Path first, final String... more) {
	return getPath(first.toString(), more);
    }

    private boolean isNotPosixFileSystem() {
	final String os = System.getProperty("os.name");
	return os.matches(".*Windows.*");
    }

}
