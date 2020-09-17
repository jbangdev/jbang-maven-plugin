package dev.jbang;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;

/**
 * Run JBang with the specified parameters
 *
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class RunMojo extends AbstractMojo {

    private static final boolean IS_OS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows");

    private static final int OK_EXIT_CODE = 0;

    /**
     * Location of the JBang script to use
     */
    @Parameter(property = "script", required = true)
    private String script;

    /**
     * The arguments to be used in the JBang script
     */
    @Parameter(property = "jbang.args")
    private String[] args;


    /**
     * If the script is in a remote location, what URLs should be trusted
     *
     * See https://github.com/jbangdev/jbang#urls-from-trusted-sources for more information
     */
    @Parameter(property = "jbang.trusts")
    private String[] trusts;

    /**
     * JBang Version to use. This version is only used to download the JBang binaries if nothing is found in the PATH
     */
    @Parameter(property = "jbang.version")
    private String jbangVersion = getPluginVersion();

    // Used in MojoExecutor. See RunMojo#download
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}")
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    private Path jbangHome;

    @Override
    public void execute() throws MojoExecutionException {
        detectJBang();
        executeTrust();
        executeJBang();
    }

    private void detectJBang() throws MojoExecutionException {
        ProcessResult result = version();
        if (result.getExitValue() == OK_EXIT_CODE) {
            getLog().info("Found JBang v." + result.outputString());
        } else {
            getLog().warn("JBang not found. Downloading version " + jbangVersion);
            download();
            result = version();
            if (result.getExitValue() == OK_EXIT_CODE) {
                getLog().info("Using JBang v." + result.outputString());
            }
        }
    }

    private String getPluginVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    private void download() throws MojoExecutionException {
        String uri = String.format("https://github.com/jbangdev/jbang/releases/download/v%s/jbang-%s.zip",
                                   jbangVersion, jbangVersion);
        executeMojo(
                plugin("com.googlecode.maven-download-plugin",
                       "download-maven-plugin",
                       "1.6.0"),
                goal("wget"),
                configuration(
                        element("uri", uri),
                        element("unpack", "true"),
                        element("outputDirectory", "./.jbang")
                ),
                executionEnvironment(
                        project,
                        session,
                        pluginManager));
        jbangHome = Paths.get("./.jbang/jbang-" + jbangVersion);

    }

    private ProcessResult version() throws MojoExecutionException {
        List<String> command = command();
        command.add(findJBangExecutable() + " version");
        ProcessResult result = null;
        try {
            result = new ProcessExecutor()
                    .command(command)
                    .readOutput(true)
                    .destroyOnExit()
                    .execute();
        } catch (Exception e) {
            throw new MojoExecutionException("Error while fetching the JBang version", e);
        }
        return result;
    }


    /**
     * Execute "jbang trust add URL..."
     *
     * @throws MojoExecutionException if the exit value is different from
     *                                0: success
     *                                1: Already trusted source(s)
     */
    private void executeTrust() throws MojoExecutionException {
        if (trusts == null || trusts.length == 0) {
            // No trust required
            return;
        }
        List<String> command = command();
        command.add(findJBangExecutable() + " trust add " + String.join(" ", trusts));
        ProcessResult result = execute(command);
        int exitValue = result.getExitValue();
        if (exitValue != 0 && exitValue != 1) {
            throw new MojoExecutionException("Error while trusting JBang URLs. Exit code: " + result.getExitValue());
        }
    }

    /**
     * Execute jbang run script arguments
     *
     * @throws MojoExecutionException if exit value != 0
     */
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

    /**
     * @return the command containing the supported shell per OS
     */
    private List<String> command() {
        List<String> command = new ArrayList<>();
        if (IS_OS_WINDOWS) {
            command.add("cmd.exe");
            command.add("/c");
        } else {
            command.add("sh");
            command.add("-c");
        }
        return command;
    }

    private String findJBangExecutable() {
        if (jbangHome != null) {
            if (IS_OS_WINDOWS) {
                return jbangHome.resolve("bin/jbang.bat").toString();
            } else {
                return jbangHome.resolve("bin/jbang").toString();
            }
        } else {
            if (IS_OS_WINDOWS) {
                return "jbang.bat";
            } else {
                return "jbang";
            }
        }
    }
}
