package org.metalib.maven.plugin.json.path;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.metalib.maven.plugin.json.path.model.JsonInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Mojo(name = "paths", defaultPhase = LifecyclePhase.NONE)
public class JsonPathMojo extends AbstractMojo {

    static final String DOT_GIT = ".git";
    static final String DOT_GITIGNORE = ".gitignore";
    static final String DOT_ENV = ".env";

    @Parameter(property = "paths")
    JsonInput[] paths;

    @Parameter( defaultValue = "${project}", readonly = true)
    MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException {
        if (null == paths) {
            throw new MojoExecutionException("No paths provided");
        }

        // Collecting output properties
        final var output = new Properties();
        for (final var path : paths) {
            final var file = path.getFile();
            final var pathText = null != file && file.isFile()
                    ? readFile(file)
                    : path.getText();
            final var jsonParsed = JsonPath.parse(pathText);
            for (final var entry : path.getQueryMaps().entrySet()) {
                output.setProperty(entry.getKey().toString(), jsonParsed.read(entry.getValue().toString(), String.class));
            }
        }

        // Updating .env file
        final var dotEnvFile = new File(mavenProject.getBasedir(), DOT_ENV);
        final var dotEnv = new Properties();
        if (dotEnvFile.isFile()) {
            try (var inputStream = Files.newInputStream(dotEnvFile.toPath())) {
                dotEnv.load(inputStream);
            } catch (IOException e) {
                throw new MojoExecutionException(e);
            }
        }
        if (dotEnvFile.isFile() || !dotEnvFile.exists()) {
            try (var outputStream = Files.newOutputStream(dotEnvFile.toPath())) {
                output.store(outputStream, null);
            } catch (IOException e) {
                throw new MojoExecutionException(e);
            }
        }

        // Updating .gitignore file
        final var dotGitFile = new File(mavenProject.getBasedir(), DOT_GIT);
        if (dotGitFile.exists()) {
            final var dotGitIgnoreFile = new File(mavenProject.getBasedir(), DOT_GITIGNORE);
            final var dotGitIgnoreLines = dotGitIgnoreFile.exists()
                    ? readAllLines(dotGitIgnoreFile)
                    : new ArrayList<String>();
            if (dotGitIgnoreLines.stream().noneMatch(v -> v.equals(DOT_ENV))) {
                dotGitIgnoreLines.add("");
                dotGitIgnoreLines.add("### protecting .env file ###");
                dotGitIgnoreLines.add(DOT_ENV);
                write(dotGitIgnoreFile, dotGitIgnoreLines);
            }
        }
    }

    @SneakyThrows
    public static void write(File file, List<String> lines) {
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    @SneakyThrows
    public static String readFile(File file) {
        return Files.readString(file.toPath());
    }

    @SneakyThrows
    static List<String> readAllLines(File file) {
        return Files.readAllLines(file.toPath());
    }
}
