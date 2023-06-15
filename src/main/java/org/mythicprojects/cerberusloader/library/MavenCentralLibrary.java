package org.mythicprojects.cerberusloader.library;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public class MavenCentralLibrary extends Library {

    public MavenCentralLibrary(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        super("https://repo.maven.apache.org/maven2", groupId, artifactId, version);
    }

    public static @NotNull MavenCentralLibrary of(@NotNull DefaultLibrary dependency, @NotNull String configurationVersion) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();

        String version = configurationVersion;
        switch (configurationVersion.toLowerCase(Locale.ROOT)) {
            case "legacy":
                version = dependency.getLegacyVersion();
                break;
            case "modern":
                version = dependency.getModernVersion();
                break;
        }

        return new MavenCentralLibrary(groupId, artifactId, version);
    }

}
