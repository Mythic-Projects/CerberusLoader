package org.mythicprojects.cerberusloader.dependency;

import org.jetbrains.annotations.NotNull;

public enum DefaultDependency {

    NETTY("io.netty", "netty-all", "4.0.23.Final", "4.1.93.Final"),
    GUAVA("com.google.guava", "guava", "17.0", "21.0"),
    GSON("com.google.code.gson", "gson", "2.2.4", "2.8.9"),
    SNAKEYAML("org.yaml", "snakeyaml", "1.15", "1.30"),
    MYSQL_DRIVER("mysql", "mysql-connector-java", "5.1.14", "8.0.22");

    private final String groupId;
    private final String artifactId;
    private final String modernVersion;
    private final String legacyVersion;

    DefaultDependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String modernVersion, @NotNull String legacyVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.modernVersion = modernVersion;
        this.legacyVersion = legacyVersion;
    }

    public @NotNull String getGroupId() {
        return this.groupId;
    }

    public @NotNull String getArtifactId() {
        return this.artifactId;
    }

    public @NotNull String getModernVersion() {
        return this.modernVersion;
    }

    public @NotNull String getLegacyVersion() {
        return this.legacyVersion;
    }

}
