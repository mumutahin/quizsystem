package com.quizapp.utils;

import java.io.File;
import java.net.URISyntaxException;

public class Constants {
    public static final String APP_NAME = "Quiz System";
    public static final String DB_NAME;

    static {
        try {
            // Start from where classes/jar are loaded
            File appLocation = new File(Constants.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            // If running from a JAR, appLocation = quizsystem/quizsystem.jar
            // If running from IDE, appLocation = quizsystem/target/classes
            File projectRoot = appLocation.isFile()
                    ? appLocation.getParentFile()      // jar → parent folder = quizsystem/
                    : appLocation.getParentFile().getParentFile(); // classes → go up twice to quizsystem/

            // Create /data folder in project root
            File dataFolder = new File(projectRoot, "data");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            // Final DB path
            DB_NAME = "jdbc:sqlite:" + new File(dataFolder, "quizsystem.db").getAbsolutePath();

        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve DB path", e);
        }
    }
}
