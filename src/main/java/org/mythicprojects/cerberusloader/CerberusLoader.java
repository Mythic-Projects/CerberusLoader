package org.mythicprojects.cerberusloader;

import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.config.LoaderConfiguration;
import org.mythicprojects.cerberusloader.dependency.DefaultDependency;
import org.mythicprojects.cerberusloader.dependency.DependencyDownloader;
import org.mythicprojects.cerberusloader.dependency.MavenCentralDependency;
import org.mythicprojects.cerberusloader.logging.CerberusLogger;
import org.mythicprojects.cerberusloader.logging.CerberusLoggerFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class CerberusLoader {

    private static final Logger DEFAULT_LOGGER = Logger.getLogger("CerberusLoader");
    private final CerberusLogger logger = new CerberusLogger(DEFAULT_LOGGER);

    private final DependencyDownloader dependencyDownloader = new DependencyDownloader();

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new CerberusLoggerFormatter());
        DEFAULT_LOGGER.setUseParentHandlers(false);
        DEFAULT_LOGGER.addHandler(handler);
    }

    public void start(String[] args) {
        LoaderConfiguration configuration = LoaderConfiguration.createConfiguration(FileConsts.CONFIGURATION_FILE);
        LoaderConfiguration.Dependencies dependenciesConfiguration = configuration.dependencies;

        Collection<File> files = this.downloadDefaultDependencies(dependenciesConfiguration);
        URL[] urls = files.stream().map(file -> {
            try {
                return file.toURI().toURL();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).toArray(URL[]::new);
        URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

        try {
            Class.forName("org.bukkit.craftbukkit.Main", true, classLoader).getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.error("Failed to load Bukkit", ex);
        }
    }

    private Collection<File> downloadDefaultDependencies(@NotNull LoaderConfiguration.Dependencies dependenciesConfiguration) {
        List<MavenCentralDependency> dependencies = Arrays.asList(
                MavenCentralDependency.of(DefaultDependency.NETTY, dependenciesConfiguration.netty),
                MavenCentralDependency.of(DefaultDependency.GUAVA, dependenciesConfiguration.guava),
                MavenCentralDependency.of(DefaultDependency.GSON, dependenciesConfiguration.gson),
                MavenCentralDependency.of(DefaultDependency.SNAKEYAML, dependenciesConfiguration.snakeYaml),
                MavenCentralDependency.of(DefaultDependency.MYSQL_DRIVER, dependenciesConfiguration.mysqlDriver)
        );

        this.logger.info("Downloading dependencies...");
        return dependencies.stream()
                .map(dependency -> {
                    try {
                        return this.dependencyDownloader.findOrDownloadDependency(dependency);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

}
