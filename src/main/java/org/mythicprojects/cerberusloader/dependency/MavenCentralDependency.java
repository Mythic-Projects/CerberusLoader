package org.mythicprojects.cerberusloader.dependency;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public class MavenCentralDependency extends Dependency {

    public MavenCentralDependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        super("https://repo.maven.apache.org/maven2", groupId, artifactId, version);
    }

    public static @NotNull MavenCentralDependency of(@NotNull DefaultDependency dependency, @NotNull String configurationVersion) {
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

        return new MavenCentralDependency(groupId, artifactId, version);
    }

}
