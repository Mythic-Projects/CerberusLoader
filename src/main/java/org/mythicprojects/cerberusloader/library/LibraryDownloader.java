package org.mythicprojects.cerberusloader.library;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.CerberusLoader;
import org.mythicprojects.cerberusloader.LoaderConst;
import org.mythicprojects.cerberusloader.library.maven.MavenDependency;
import org.mythicprojects.cerberusloader.library.maven.MavenProject;
import org.mythicprojects.cerberusloader.util.ObjectsHelper;

import static java.util.Objects.requireNonNull;
import static org.mythicprojects.cerberusloader.util.ObjectsHelper.firstNonNull;

public class LibraryDownloader {

    public @NotNull Set<File> findOrDownloadDependency(@NotNull Library toLoad) throws IOException {
        return this.findOrDownloadDependency(toLoad, true);
    }

    public @NotNull Set<File> findOrDownloadDependency(@NotNull Library toLoad, boolean deep) throws IOException {
        Set<File> dependencies = new LinkedHashSet<>();
        dependencies.add(this.findOrDownloadDependencyFile(toLoad, "jar"));
        if (!deep) {
            return dependencies;
        }

        File pomFile = this.findOrDownloadDependencyFile(toLoad, "pom");
        ObjectMapper xmlMapper = new XmlMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        MavenProject mavenProject = xmlMapper.readValue(pomFile, MavenProject.class);
        MavenDependency projectParent = mavenProject.getParent();
        String projectGroupId = ObjectsHelper.safeNull(mavenProject, projectParent, MavenDependency::getGroupId);
        String projectVersion = ObjectsHelper.safeNull(mavenProject, projectParent, MavenDependency::getVersion);

        mavenProject.getDependencies().stream()
                .filter(dependency -> !dependency.isOptional())
                .filter(dependency -> dependency.getScope() != null && dependency.getScope().equals("compile"))
                .forEachOrdered(dependency -> {
                    String groupId = firstNonNull(dependency.getGroupId(), projectGroupId);
                    String artifactId = requireNonNull(dependency.getArtifactId());
                    String version = firstNonNull(dependency.getVersion(), projectVersion);

                    groupId = groupId.replace("${project.groupId}", Objects.toString(projectGroupId));
                    version = version.replace("${project.version}", Objects.toString(projectVersion));

                    MavenCentralLibrary library = new MavenCentralLibrary(groupId, artifactId, version);
                    try {
                        dependencies.addAll(this.findOrDownloadDependency(library));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        return dependencies;
    }

    private @NotNull File findOrDownloadDependencyFile(@NotNull Library library, @NotNull String extension) throws IOException {
        String filePath = library.prepareFilePath(extension);

        Path dependencyPath = Paths.get(LoaderConst.DEPENDENCIES_DIRECTORY.getAbsolutePath(), filePath);
        File dependencyFile = dependencyPath.toFile();
        if (dependencyFile.exists()) {
            return dependencyFile;
        }

        File dependencyDirectory = dependencyFile.getParentFile();
        if (!dependencyDirectory.exists() && !dependencyDirectory.mkdirs()) {
            throw new IOException("Failed to create dependency directory");
        }

        if (!dependencyFile.createNewFile()) {
            throw new IOException("Failed to create dependency file");
        }

        ReadableByteChannel readableByteChannel = Channels.newChannel(library.prepareFileUrl(extension).openStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(dependencyFile)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return dependencyFile;
    }

    public @NotNull Set<MavenCentralLibrary> readLibraries(@NotNull Path jarPath) throws IOException {
        if (!Files.exists(jarPath)) {
            throw new IllegalArgumentException("Jar file does not exist");
        }

        Set<MavenCentralLibrary> libraries = new LinkedHashSet<>();
        try (FileSystem fileSystem = FileSystems.newFileSystem(jarPath, CerberusLoader.class.getClassLoader())) {
            Path fileToExtract = fileSystem.getPath("libraries.cerberus");
            if (!Files.exists(fileToExtract)) {
                System.out.println("No libraries.cerberus file found in jar");
                return libraries;
            }
            try (InputStream inputStream = Files.newInputStream(fileToExtract);
                 InputStreamReader inputReader = new InputStreamReader(inputStream);
                 BufferedReader bufferedReader = new BufferedReader(inputReader)) {

                bufferedReader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .forEachOrdered(line -> {
                            String[] split = line.split(":");
                            if (split.length != 3) {
                                System.out.println("Invalid line in libraries.cerberus file: " + line);
                                return;
                            }
                            String groupId = split[0];
                            String artifactId = split[1];
                            String version = split[2];
                            libraries.add(new MavenCentralLibrary(groupId, artifactId, version));
                        });
            }
        }
        return libraries;
    }

}
