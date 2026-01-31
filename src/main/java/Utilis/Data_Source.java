package Utilis;

import java.sql.Connection;
import java.sql.DriverManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class Data_Source {
    private static Data_Source instance; // Singleton instance
    private String username;
    private String password;
    private String url;
    private Connection con;
    LogManager logger = LogManager.getInstance();

    // Private constructor for Singleton
    private Data_Source() {
        try {
            // Load JSON file from the classpath
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("JSON_FILES/Database_config.json");
            if (inputStream == null) {
                throw new RuntimeException("JSON configuration file not found on the classpath.");
            }

            // Parse the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Extract connection details from the JSON file
            this.username = rootNode.get("username").asText();
            this.password = rootNode.get("password").asText();
            this.url = rootNode.get("url").asText();

            // Establish the connection
            con = DriverManager.getConnection(url, username, password);
            logger.info("Connection established !");
            // System.out.println("Connection established");
        } catch (Exception e) {
            e.printStackTrace(); // Log the error to console
            System.err.println("Database Connection error: " + e.getMessage());
        }
    }

    // Get the singleton instance
    public static synchronized Data_Source getInstance() {
        if (instance == null) {
            instance = new Data_Source();
        }
        return instance;
    }

    // Getters for the connection and properties
    public Connection getConnection() {
        return con;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
