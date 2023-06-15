package org.mythicprojects.cerberusloader.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public class LoaderConfiguration extends OkaeriConfig {

    public String licenseKeyFilePath = "cerberus/license.key";

    @Comment("Versions of libraries used to load the UnicornSpigot server")
    @Comment("LEGACY -> Best compatibility with old plugins, but may be more vulnerable")
    @Comment("MODERN -> Worst compatibility with old plugins, but may be more secure and provide modern features and performance improvements")
    @Comment("CUSTOM (providing specific version) -> You can provide specific version of library, don't do that if you don't know what you are doing")
    public Libraries libraries = new Libraries();

    public static class Libraries extends OkaeriConfig {

        @Comment("")
        @Comment("LEGACY -> 4.0.23.Final (1.8.8)")
        @Comment("MODERN -> 4.1.91.Final")
        @Comment("WARNING: For Java 9+ MODERN version will be forced")
        public String netty = "LEGACY";

        @Comment("")
        @Comment("LEGACY -> 17.0 (1.8.8)")
        @Comment("MODERN -> 21.0")
        public String guava = "LEGACY";

        @Comment("")
        @Comment("LEGACY -> 2.2.4 (1.8.8)")
        @Comment("MODERN -> 2.8.9")
        public String gson = "LEGACY";

        @Comment("")
        @Comment("LEGACY -> 1.15 (1.8.8)")
        @Comment("MODERN -> 1.30")
        public String snakeYaml = "LEGACY";

        @Comment("")
        @Comment("LEGACY -> 5.1.14 (1.8.8)")
        @Comment("MODERN -> 8.0.22")
        public String mysqlDriver = "LEGACY";

    }

    public static @NotNull LoaderConfiguration createConfiguration(@NotNull File file) {
        return ConfigManager.create(LoaderConfiguration.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(file);

            it.saveDefaults();
            it.load(true);
        });
    }

}
