import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

Path path = Paths.get(basedir.toString(), "build.log" );
assert new String(Files.readAllBytes(path)).contains("--option1=foo")
