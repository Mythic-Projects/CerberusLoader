package org.mythicprojects.cerberusloader.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MavenCentralDependency extends Dependency {

    public MavenCentralDependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        super("https://repo.maven.apache.org/maven2", groupId, artifactId, version);
    }

    public static @NotNull MavenCentralDependency of(@NotNull DefaultDependency dependency, @NotNull String configurationVersion) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();

        String version = configurationVersion;
        switch (configurationVersion.toLowerCase(Locale.ROOT)) {
            case "modern":
                version = dependency.getModernVersion();
                break;
            case "legacy":
                version = dependency.getLegacyVersion();
                break;
        }

        return new MavenCentralDependency(groupId, artifactId, version);
    }

}
