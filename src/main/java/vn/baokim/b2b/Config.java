package vn.baokim.b2b;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config Manager - Quản lý cấu hình
 */
public class Config {
    private static Properties properties = null;
    private static String basePath = "";
    
    /**
     * Load config từ file
     */
    public static void load() throws Exception {
        load("");
    }
    
    public static void load(String path) throws Exception {
        basePath = path;
        properties = new Properties();
        
        // Try config.local.properties first
        String localPath = path + "src/main/resources/config.local.properties";
        String defaultPath = path + "src/main/resources/config.properties";
        
        InputStream is = null;
        try {
            is = new FileInputStream(localPath);
        } catch (Exception e) {
            try {
                is = new FileInputStream(defaultPath);
            } catch (Exception e2) {
                // Try from classpath
                is = Config.class.getClassLoader().getResourceAsStream("config.properties");
            }
        }
        
        if (is == null) {
            throw new Exception("Config file not found");
        }
        
        properties.load(is);
        is.close();
    }
    
    /**
     * Lấy giá trị config
     */
    public static String get(String key) {
        return get(key, null);
    }
    
    public static String get(String key, String defaultValue) {
        if (properties == null) {
            try {
                load();
            } catch (Exception e) {
                return defaultValue;
            }
        }
        String value = properties.getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static String getBasePath() {
        return basePath;
    }
}
