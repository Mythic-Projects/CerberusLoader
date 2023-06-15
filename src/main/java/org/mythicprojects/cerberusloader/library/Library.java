package org.mythicprojects.cerberusloader.library;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Library {

    private final String repository;

    private final String groupId;
    private final String artifactId;
    private final String version;

    public Library(@NotNull String repository, @NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        this.repository = repository;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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

    public @NotNull String prepareFilePath(@NotNull String extension) {
        return String.format("%s/%s/%s/%s-%s.%s", this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version, extension);
    }

    public @NotNull URL prepareFileUrl(@NotNull String extension) throws MalformedURLException {
        return new URL(String.format("%s/%s", this.repository, this.prepareFilePath(extension)));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Library that = (Library) obj;
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

}
