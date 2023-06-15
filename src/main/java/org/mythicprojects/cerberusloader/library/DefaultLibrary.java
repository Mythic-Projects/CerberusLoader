package org.mythicprojects.cerberusloader.library;

import org.jetbrains.annotations.NotNull;

public enum DefaultLibrary {

    NETTY("io.netty", "netty-all", "4.0.23.Final", "4.1.91.Final"),
    GUAVA("com.google.guava", "guava", "17.0", "21.0"),
    GSON("com.google.code.gson", "gson", "2.2.4", "2.8.9"),
    SNAKEYAML("org.yaml", "snakeyaml", "1.15", "1.30"),
    MYSQL_DRIVER("mysql", "mysql-connector-java", "5.1.14", "8.0.22");

    private final String groupId;
    private final String artifactId;
    private final String legacyVersion;
    private final String modernVersion;

    DefaultLibrary(@NotNull String groupId, @NotNull String artifactId, @NotNull String legacyVersion, @NotNull String modernVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.legacyVersion = legacyVersion;
        this.modernVersion = modernVersion;
    }

    public @NotNull String getGroupId() {
        return this.groupId;
    }

    public @NotNull String getArtifactId() {
        return this.artifactId;
    }

    public @NotNull String getLegacyVersion() {
        return this.legacyVersion;
    }

    public @NotNull String getModernVersion() {
        return this.modernVersion;
    }

}
