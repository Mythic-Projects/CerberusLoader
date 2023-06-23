package org.mythicprojects.cerberusloader;

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
import org.jetbrains.annotations.NotNull;
import org.mythicprojects.cerberusloader.config.LoaderConfiguration;
import org.mythicprojects.cerberusloader.library.DefaultLibrary;
import org.mythicprojects.cerberusloader.library.Library;
import org.mythicprojects.cerberusloader.library.LibraryDownloader;
import org.mythicprojects.cerberusloader.library.MavenCentralLibrary;
import org.mythicprojects.cerberusloader.logging.CerberusLogger;
import org.mythicprojects.cerberusloader.logging.CerberusLoggerFormatter;

public final class CerberusLoader {

    private static final Logger DEFAULT_LOGGER = Logger.getLogger("CerberusLoader");
    private static final CerberusLogger LOGGER = new CerberusLogger(DEFAULT_LOGGER);

    private final LibraryDownloader libraryDownloader = new LibraryDownloader();

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new CerberusLoggerFormatter());
        DEFAULT_LOGGER.setUseParentHandlers(false);
        DEFAULT_LOGGER.addHandler(handler);
    }

    public void start(String[] args) {
        LoaderConfiguration configuration = LoaderConfiguration.createConfiguration(LoaderConst.CONFIGURATION_FILE);
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
            LOGGER.error("Failed to load Bukkit", ex);
        }
    }

    private @NotNull Collection<File> downloadDefaultDependencies(@NotNull LoaderConfiguration.Libraries librariesConfiguration) {
        List<Library> dependencies = Arrays.asList(
                MavenCentralLibrary.of(DefaultLibrary.NETTY, librariesConfiguration.netty, 9),
                MavenCentralLibrary.of(DefaultLibrary.GUAVA, librariesConfiguration.guava),
                MavenCentralLibrary.of(DefaultLibrary.GSON, librariesConfiguration.gson),
                MavenCentralLibrary.of(DefaultLibrary.SNAKEYAML, librariesConfiguration.snakeYaml),
                MavenCentralLibrary.of(DefaultLibrary.MYSQL_DRIVER, librariesConfiguration.mysqlDriver),
                new MavenCentralLibrary("org.fusesource.jansi", "jansi", "2.4.0") // Needed to use it in UnicornSpigot...
        );

        LOGGER.info("Downloading dependencies...");
        return dependencies.stream()
                .flatMap(library -> {
                    try {
                        return this.libraryDownloader.findOrDownloadDependency(library).stream();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public static @NotNull CerberusLogger getLogger() {
        return LOGGER;
    }

}
