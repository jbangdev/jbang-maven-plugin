///usr/bin/env jbang "$0" "$@" ; exit $?

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;

import static java.lang.System.*;

public class readme2index {

    public static void main(String... args) throws Exception  {
        var target = Paths.get("build/content/pages/index.adoc");
        createDirectories(target.getParent());
        copy(Paths.get("README.adoc"),target,StandardCopyOption.REPLACE_EXISTING);
        out.println("README.adoc copied to " + target);
    }
}
