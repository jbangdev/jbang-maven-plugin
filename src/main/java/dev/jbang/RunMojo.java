package dev.jbang;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

/**
 * Run JBang with the specified parameters
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class RunMojo extends AbstractMojo {

    /**
     * Location of the JBang script to use
     */
    @Parameter(required = true)
    private String script;

    /**
     * The arguments to be used in the JBang script
     */
    @Parameter
    private String[] args;


    /**
     * If the script is in a remote location, what URLs should be trusted
     *
     * {@link https://github.com/jbangdev/jbang#urls-from-trusted-sources} for more information
     */
    @Parameter
    private String[] trusts;

    /**
     * JBang Version to use
     */
    @Parameter(property = "jbang.version", defaultValue = "latest")
    private String jbangVersion;

    @Override
    public void execute() throws MojoExecutionException {
        //TODO: install JBang
        executeTrust();
        executeJBang();
    }

    private void executeTrust() throws MojoExecutionException {
        if (trusts == null || trusts.length == 0) {
            // No trust required
            return;
        }
        for (String trust : trusts) {
            List<String> command = command();
            command.add(findJBangExecutable() + " trust add " + trust);
            ProcessResult result = execute(command);
            int exitValue = result.getExitValue();
            if (exitValue != 0 && exitValue != 1) {
                throw new MojoExecutionException("Error while trusting JBang URLs. Exit code: " + result.getExitValue());
            }
        }
    }

    private void executeJBang() throws MojoExecutionException {
        List<String> command = command();
        StringBuilder executable = new StringBuilder(findJBangExecutable());
        executable.append(" run ").append(script);
        if (args != null) {
            executable.append(" ").append(String.join(" ", args));
        }
        command.add(executable.toString());
        ProcessResult result = execute(command);
        if (result.getExitValue() != 0) {
            throw new MojoExecutionException("Error while executing JBang. Exit code: " + result.getExitValue());
        }
    }

    private ProcessResult execute(List<String> command) throws MojoExecutionException {
        try {
            return new ProcessExecutor()
                    .command(command)
                    .redirectOutput(Slf4jStream.ofCaller().asInfo())
                    .destroyOnExit()
                    .execute();
        } catch (Exception e) {
            throw new MojoExecutionException("Error while executing JBang", e);
        }
    }

    private Path findJBangHome() {
        //TODO: Change it
        return Paths.get("/home/ggastald/.sdkman/candidates/jbang/0.45.0/");
    }

    private List<String> command() {
        // TODO: Change for other OSes
        List<String> command = new ArrayList<>();
        command.add("sh");
        command.add("-c");
        return command;
    }

    private String findJBangExecutable() {
        // TODO: Change it for other OSes
        return findJBangHome().resolve("bin/jbang").toString();
    }
}
