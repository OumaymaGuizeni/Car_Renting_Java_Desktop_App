package Utilis;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {
    private static volatile LogManager instance;
    private static final Object lock = new Object();
    private PrintWriter writer;
    private static final String LOG_FILE = ".app_cache/application.log";

    // Private constructor to prevent instantiation
    private LogManager() {
        try {
            // Ensure the parent directory exists
            File logFile = new File(LOG_FILE);
            File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // Create directories if they don't exist
            }


            // If the log file exists, delete it
            if (logFile.exists()) {
                if (logFile.delete()) {
                    log("INFO","Old file log is now Deleted since new session is started");
                    // System.out.println("Old log file deleted.");
                } else {
                    log("ERROR","Failed to delete the previous log file , this may cause a corapted log file ");
                    // System.err.println("Failed to delete the existing log file.");
                }
            }

            // Create the log file and open PrintWriter
            if (logFile.createNewFile()) {
                log("INFO","New Log file is Created for this new session now in .app_cache/ , application.log");
                // System.out.println("New log file created.");
            } else {
                log("ERROR","Failed to delete the previous log file , this may cause a corapted log file ");
                // System.err.println("Failed to create a new log file.");
            }

            writer = new PrintWriter(new FileWriter(logFile, true), true); // Open the file for appending
        } catch (IOException e) {
            log("ERROR","Failed to initialize LogManager : " + e.getMessage());
            // System.err.println("Failed to initialize LogManager: " + e.getMessage());
        }
    }

    // Method to get the singleton instance
    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }

    // Method to log messages
    public void log(String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Print to console
        System.out.println(logMessage);

        // Write to file
        if (writer != null) {
            writer.println(logMessage);
        }
    }

    // Convenience methods for different log levels
    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    // Clean up resources
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        LogManager logger = LogManager.getInstance();
        logger.info(" --------------------> Application started <----------------------");
        logger.warn("This is a warning message.");
        logger.error("An error occurred.");
        logger.close();
    }
}
