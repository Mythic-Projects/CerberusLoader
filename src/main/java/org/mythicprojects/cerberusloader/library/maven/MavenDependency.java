package org.mythicprojects.cerberusloader.library.maven;

import org.jetbrains.annotations.Nullable;

public class MavenDependency {

    private String groupId;
    private String artifactId;
    private String version;

    public @Nullable String getGroupId() {
        return this.groupId;
    }

    public @Nullable String getArtifactId() {
        return this.artifactId;
    }

    public @Nullable String getVersion() {
        return this.version;
    }

}
