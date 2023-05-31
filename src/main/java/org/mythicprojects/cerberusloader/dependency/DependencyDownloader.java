package org.mythicprojects.cerberusloader.dependency;

import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.FileConsts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DependencyDownloader {

    public @NotNull File findOrDownloadDependency(@NotNull Dependency dependency) throws IOException {
        Path dependencyPath = Paths.get(FileConsts.DEPENDENCIES_DIRECTORY.getAbsolutePath(), dependency.getJarPath());
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

        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(dependency.getJarUrl()).openStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(dependencyFile)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return dependencyFile;
    }

}
