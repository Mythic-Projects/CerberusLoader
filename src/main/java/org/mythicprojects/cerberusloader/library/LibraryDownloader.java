package org.mythicprojects.cerberusloader.library;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.LoaderConst;
import org.mythicprojects.cerberusloader.library.maven.MavenDependency;
import org.mythicprojects.cerberusloader.library.maven.MavenProject;

public class LibraryDownloader {

    public @NotNull Set<File> findOrDownloadDependencyDeep(@NotNull Library toLoad) throws IOException {
        Set<File> dependencies = new LinkedHashSet<>();
        dependencies.add(this.findOrDownloadDependency(toLoad));

        try (InputStream input = toLoad.prepareFileUrl("pom").openStream()) {
            ObjectMapper xmlMapper = new XmlMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            MavenProject mavenProject = xmlMapper.readValue(input, MavenProject.class);
            Set<File> subDependencies = mavenProject.getDependencies().stream()
                    .filter(dependency -> !dependency.isOptional())
                    .filter(dependency -> dependency.getScope() != null && dependency.getScope().equals("compile"))
                    .flatMap(dependency -> {
                        MavenDependency projectParent = mavenProject.getParent();
                        String projectGroupId = mavenProject.getGroupId() != null
                                ? mavenProject.getGroupId()
                                : projectParent.getGroupId();
                        String projectVersion = mavenProject.getVersion() != null
                                ? mavenProject.getVersion() :
                                projectParent.getVersion();

                        String groupId = dependency.getGroupId().replace("${project.groupId}", Objects.toString(projectGroupId));
                        String version = dependency.getVersion().replace("${project.version}", Objects.toString(projectVersion));

                        MavenCentralLibrary library = new MavenCentralLibrary(groupId, dependency.getArtifactId(), version);
                        try {
                            return this.findOrDownloadDependencyDeep(library).stream();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .collect(Collectors.toSet());
            dependencies.addAll(subDependencies);
        }

        return dependencies;
    }

    public @NotNull File findOrDownloadDependency(@NotNull Library library) throws IOException {
        String jarPath = library.prepareFilePath("jar");
        Path dependencyPath = Paths.get(LoaderConst.DEPENDENCIES_DIRECTORY.getAbsolutePath(), jarPath);
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

        ReadableByteChannel readableByteChannel = Channels.newChannel(library.prepareFileUrl("jar").openStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(dependencyFile)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return dependencyFile;
    }

}
