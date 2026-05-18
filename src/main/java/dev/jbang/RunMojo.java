package dev.jbang;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.maveniverse.maven.toolrunner.shared.Config;
import eu.maveniverse.maven.toolrunner.shared.ToolExecution;
import eu.maveniverse.maven.toolrunner.shared.ToolHandle;
import eu.maveniverse.maven.toolrunner.shared.ToolHandler;
import eu.maveniverse.maven.toolrunner.shared.ToolManager;
import eu.maveniverse.maven.toolrunner.tools.jbang.JBangProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;

/**
 * Run JBang with the specified parameters
 *
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = false)
public class RunMojo extends AbstractMojo {
    /**
     * The arguments to be used for JBang itself
     */
    @Parameter(property = "jbang.jbangargs")
    private String[] jbangargs;

    /**
     * Location of the JBang script to use
     */
    @Parameter(property = "jbang.script", required = true)
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
    @Parameter(property = "jbang.version", defaultValue = Artifact.LATEST_VERSION)
    private String jbangVersion;

    /**
     * JBang installation directory. Default location is ${project.basedir}
     */
    @Parameter(property = "jbang.install.dir", defaultValue = "${project.basedir}")
    private File jbangInstallDir;

    /**
     * Skip this execution
     */
    @Parameter(property = "jbang.skip")
    private boolean skip;

    /**
     * Used for basedir -> CWD
     */
    @Inject
    protected MavenSession mavenSession;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping plugin execution");
            return;
        }
        Path tempDir = null; // use default
        Path cwd = Paths.get(mavenSession.getExecutionRootDirectory());
        if (mavenSession.getCurrentProject().getBasedir() != null) {
            Path output = Paths.get(mavenSession.getCurrentProject().getBuild().getOutputDirectory());
            tempDir = output.resolve("toolrunner-tmp");
            cwd = mavenSession.getCurrentProject().getBasedir().toPath();
        }
        try (ToolManager toolManager = ToolManager.create(
                Config.builder()
                        .isTransient(false)
                        .allowPathDetection(true)
                        .tempDirectory(tempDir)
                        .installationDirectory(jbangInstallDir != null ? jbangInstallDir.toPath() : null)
                        .build())) {
            ToolHandler toolHandler = toolManager.selectToolByName("jbang")
                    .orElseThrow(() -> new IllegalStateException("ToolHandler not found")); // never happens
            ToolHandle jbang;
            if (jbangVersion != null && !Artifact.LATEST_VERSION.equals(jbangVersion)) {
                // parameterize version; user wants exactly specified version
                HashMap<String, String> jbangMd = new HashMap<>();
                jbangMd.put(ToolHandler.TOOL_NAME, JBangProvider.NAME);
                jbangMd.put(ToolHandler.TOOL_VERSION, jbangVersion);
                jbang = toolHandler.selectTool(jbangMd).orElseThrow(() -> new IllegalStateException("JBang not found"));
            } else {
                // use whatever (ie $PATH) or provision LATEST
                jbang = toolHandler.toolHandle();
            }
            executeTrust(jbang);

            // execute it
            List<String> arguments = new ArrayList<>();
            arguments.add("run");
            if (jbangargs != null) {
                arguments.addAll(Arrays.asList(jbangargs));
            }
            arguments.add(script);
            if (args != null) {
                arguments.addAll(Arrays.asList(args));
            }
            ToolExecution.Builder execution = jbang.executionTemplate()
                    .cwd(cwd)
                    .arguments(arguments);
            ToolHandle.Result result = jbang.execute(execution.build());
            // we know JBangProvider uses ProcessBuilderExecutor so we insist on exitCode
            int exitCode = result.exitCode().orElseThrow(() -> new NoSuchElementException("Missing exitCode"));
            if (exitCode != 0) {
                throw new MojoExecutionException("Error while executing JBang.\nstdout: "
                        + result.stdOutString().orElse("") + "\nstderr:" + result.stdErrString().orElse(""));
            } else {
                getLog().info("JBang executed successfully");
                getLog().info(result.stdOutString().orElse(""));
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Execute "jbang trust add URL..."
     *
     * @throws MojoExecutionException if the exit value is different from
     *                                0: success
     *                                1: Already trusted source(s)
     */
    private void executeTrust(ToolHandle jbang) throws MojoExecutionException {
        if (trusts == null || trusts.length == 0) {
            // No trust required
            return;
        }
        ToolHandle.Result result = jbang.execute(jbang.executionTemplate()
                        .arguments(Stream.concat(Stream.of("trust", "add"), Arrays.stream(trusts)).collect(Collectors.toList()))
                        .build());
        // we know JBangProvider uses ProcessBuilderExecutor so we insist on exitCode
        int exitCode = result.exitCode().orElseThrow(() -> new NoSuchElementException("Missing exitCode"));
        if (exitCode != 0 && exitCode != 1) {
            throw new MojoExecutionException("Error while trusting JBang URLs.\nstdout: "
                    + result.stdOutString().orElse("") + "\nstderr:" + result.stdErrString().orElse(""));
        }
    }
}
