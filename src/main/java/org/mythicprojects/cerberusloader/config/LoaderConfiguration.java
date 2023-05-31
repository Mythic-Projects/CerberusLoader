package org.mythicprojects.cerberusloader.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class LoaderConfiguration extends OkaeriConfig {

    public String licenseKeyFilePath = "cerberus/license.key";

    public Dependencies dependencies = new Dependencies();

    public static class Dependencies extends OkaeriConfig {

        public String netty = "LEGACY";

        public String guava = "LEGACY";

        public String gson = "LEGACY";

        public String snakeYaml = "LEGACY";

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
