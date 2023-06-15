package org.mythicprojects.cerberusloader.library.maven;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MavenProject extends MavenDependency {

    private MavenDependency parent;
    private List<ProjectDependency> dependencies = Collections.emptyList();

    @Override
    public @Nullable String getGroupId() {
        return super.getGroupId();
    }

    @Override
    public @Nullable String getArtifactId() {
        return super.getArtifactId();
    }

    @Override
    public @Nullable String getVersion() {
        return super.getVersion();
    }

    public @Nullable MavenDependency getParent() {
        return this.parent;
    }

    public @NotNull List<ProjectDependency> getDependencies() {
        return this.dependencies;
    }

    public static class ProjectDependency extends MavenDependency {

        private String scope;
        private boolean optional;

        @Override
        public @NotNull String getGroupId() {
            return Objects.requireNonNull(super.getGroupId());
        }

        @Override
        public @NotNull String getArtifactId() {
            return Objects.requireNonNull(super.getArtifactId());
        }

        @Override
        public @NotNull String getVersion() {
            return Objects.requireNonNull(super.getVersion());
        }

        public @Nullable String getScope() {
            return this.scope;
        }

        public boolean isOptional() {
            return this.optional;
        }

    }


}
