package org.mythicprojects.cerberusloader;

import java.io.File;

public final class FileConsts {

    private FileConsts() {
    }

    public static final File LOADER_DIRECTORY = new File("cerberus");
    public static final File CONFIGURATION_FILE = new File(LOADER_DIRECTORY, "cerberus.yml");

    public static final File DEPENDENCIES_DIRECTORY = new File("libraries");

}
