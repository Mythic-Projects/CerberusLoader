package org.mythicprojects.cerberusloader.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dependency {

    private final String repository;

    private final String groupId;
    private final String artifactId;
    private final String version;

    private final String jarPath;

    public Dependency(@NotNull String repository, @NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        this.repository = repository;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;

        this.jarPath = prepareJarPath(groupId, artifactId, version);
    }

    public @NotNull String getRepository() {
        return this.repository;
    }

    public @NotNull String getGroupId() {
        return this.groupId;
    }

    public @NotNull String getArtifactId() {
        return this.artifactId;
    }

    public @NotNull String getVersion() {
        return this.version;
    }

    public String getJarPath() {
        return this.jarPath;
    }

    public @NotNull String getJarUrl() {
        return this.repository + "/" + this.jarPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Dependency that = (Dependency) obj;
        return Objects.equals(this.groupId, that.groupId) && Objects.equals(this.artifactId, that.artifactId) && Objects.equals(this.version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupId, this.artifactId, this.version);
    }

    @Override
    public @NotNull String toString() {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }

    private static String prepareJarPath(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        return String.format("%s/%s/%s/%s-%s.jar", groupId.replace(".", "/"), artifactId, version, artifactId, version);
    }

}
