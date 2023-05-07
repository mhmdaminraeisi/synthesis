package ir.teias;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static final String sqlUser;
    public static final String sqlPassword;
    public static final String sqlUrl;
    public static final String databaseCatalog;
    static {
        try {
            String filePath = new File("").getAbsolutePath();
            filePath += "/src/main/resources/application.properties";
            FileInputStream inputStream = new FileInputStream(filePath);
            Properties config = new Properties();
            config.load(inputStream);
            sqlUser = config.getProperty("spring.datasource.username");
            sqlPassword = config.getProperty("spring.datasource.password");
            sqlUrl = config.getProperty("spring.datasource.url");
            databaseCatalog = config.getProperty("database.catalog");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
