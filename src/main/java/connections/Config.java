package connections;

import java.io.IOException;
import java.util.Properties;

/**
 * Reads config.properties
 */
public abstract class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            ClassLoader loader = Config.class.getClassLoader();
            properties.load(loader.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static final String DATABASE_HOST = properties.getProperty("database_host");
    public static final int DATABASE_PORT = Integer.parseInt(properties.getProperty("database_port"));
    public static final String DATABASE_NAME = properties.getProperty("database_name");

    public static final String APP_ID = properties.getProperty("app_id");
    public static final String KEY = properties.getProperty("key");
    public static final String SECRET = properties.getProperty("secret");
}

