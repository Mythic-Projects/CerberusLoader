package org.mythicprojects.cerberusloader;

import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.config.LoaderConfiguration;
import org.mythicprojects.cerberusloader.library.DefaultLibrary;
import org.mythicprojects.cerberusloader.library.Library;
import org.mythicprojects.cerberusloader.library.LibraryDownloader;
import org.mythicprojects.cerberusloader.library.MavenCentralLibrary;
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

    private final LibraryDownloader libraryDownloader = new LibraryDownloader();

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new CerberusLoggerFormatter());
        DEFAULT_LOGGER.setUseParentHandlers(false);
        DEFAULT_LOGGER.addHandler(handler);
    }

    public void start(String[] args) {
        LoaderConfiguration configuration = LoaderConfiguration.createConfiguration(FileConsts.CONFIGURATION_FILE);
        LoaderConfiguration.Libraries librariesConfiguration = configuration.libraries;

        Collection<File> files = this.downloadDefaultDependencies(librariesConfiguration);
        files.add(new File("unicornspigot.jar"));
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

    private Collection<File> downloadDefaultDependencies(@NotNull LoaderConfiguration.Libraries librariesConfiguration) {
        List<Library> dependencies = Arrays.asList(
                MavenCentralLibrary.of(DefaultLibrary.NETTY, librariesConfiguration.netty),
                MavenCentralLibrary.of(DefaultLibrary.GUAVA, librariesConfiguration.guava),
                MavenCentralLibrary.of(DefaultLibrary.GSON, librariesConfiguration.gson),
                MavenCentralLibrary.of(DefaultLibrary.SNAKEYAML, librariesConfiguration.snakeYaml),
                MavenCentralLibrary.of(DefaultLibrary.MYSQL_DRIVER, librariesConfiguration.mysqlDriver)
        );

        this.logger.info("Downloading dependencies...");
        return dependencies.stream()
                .flatMap(library -> {
                    try {
                        return this.libraryDownloader.findOrDownloadDependencyDeep(library).stream();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

}
