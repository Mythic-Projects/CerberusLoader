package org.mythicprojects.cerberusloader.library;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.CerberusLoader;
import org.mythicprojects.cerberusloader.util.JavaHelper;

public class MavenCentralLibrary extends Library {

    public MavenCentralLibrary(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        super("https://repo.maven.apache.org/maven2", groupId, artifactId, version);
    }

    public static @NotNull MavenCentralLibrary of(@NotNull DefaultLibrary dependency, @NotNull String configurationVersion, int javaForcingModern) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();

        String version = configurationVersion;

        int currentJavaVersion = JavaHelper.getVersion();
        if (javaForcingModern >= 0 && currentJavaVersion >= javaForcingModern && version.equalsIgnoreCase("legacy")) {
            CerberusLoader.getLogger().warn("Forcing modern version of %s due to using Java %d", dependency.name(), currentJavaVersion);
        }

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

    public static @NotNull MavenCentralLibrary of(@NotNull DefaultLibrary dependency, @NotNull String configurationVersion) {
        return of(dependency, configurationVersion, -1);
    }

}
