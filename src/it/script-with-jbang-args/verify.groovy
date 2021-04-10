import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

Path path = Paths.get(basedir.toString(), "build.log" );
def logs = new String(Files.readAllBytes(path))
assert logs.contains("--option1=foo")
assert logs.contains("foo => bar")