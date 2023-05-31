package org.mythicprojects.cerberusloader.logging;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class CerberusLogger {

    private final Logger logger;

    public CerberusLogger(@NotNull Logger logger) {
        this.logger = logger;
    }

    public void info(@NotNull String message, @NotNull Object... args) {
        this.logger.info(String.format(message, args));
    }

    public void warn(@NotNull String message, @NotNull Object... args) {
        this.logger.warning(String.format(message, args));
    }

    public void error(@NotNull String message, @NotNull Object... args) {
        this.logger.severe(String.format(message, args));
    }

}
