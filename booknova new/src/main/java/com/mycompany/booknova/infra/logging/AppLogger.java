package com.mycompany.booknova.infra.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom application logger that writes to app.log file.
 * Handles activity logging and error logging with different levels.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class AppLogger {
    
    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static AppLogger instance;
    
    private AppLogger() {
        // Initialize log file if it doesn't exist
        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.createNewFile();
                logInfo("APPLICATION", "Log file initialized: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize log file: " + e.getMessage());
        }
    }
    
    /**
     * Gets the singleton instance of AppLogger.
     * 
     * @return the unique instance of AppLogger
     */
    public static synchronized AppLogger getInstance() {
        if (instance == null) {
            instance = new AppLogger();
        }
        return instance;
    }
    
    /**
     * Logs an informational message.
     * 
     * @param component the component or class generating the log
     * @param message the message to log
     */
    public void logInfo(String component, String message) {
        writeLog("INFO", component, message, null);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param component the component or class generating the log
     * @param message the message to log
     */
    public void logWarning(String component, String message) {
        writeLog("WARN", component, message, null);
    }
    
    /**
     * Logs an error message.
     * 
     * @param component the component or class generating the log
     * @param message the message to log
     */
    public void logError(String component, String message) {
        writeLog("ERROR", component, message, null);
    }
    
    /**
     * Logs an error message with exception details.
     * 
     * @param component the component or class generating the log
     * @param message the message to log
     * @param throwable the exception that occurred
     */
    public void logError(String component, String message, Throwable throwable) {
        writeLog("ERROR", component, message, throwable);
    }
    
    /**
     * Logs a successful operation.
     * 
     * @param component the component or class generating the log
     * @param operation the operation that was successful
     */
    public void logSuccess(String component, String operation) {
        writeLog("SUCCESS", component, operation, null);
    }
    
    /**
     * Logs user activity.
     * 
     * @param user the user performing the action
     * @param action the action performed
     */
    public void logUserActivity(String user, String action) {
        writeLog("ACTIVITY", "USER", user + " - " + action, null);
    }
    
    /**
     * Writes the log entry to the file.
     * 
     * @param level the log level
     * @param component the component generating the log
     * @param message the message to log
     * @param throwable optional exception details
     */
    private synchronized void writeLog(String level, String component, String message, Throwable throwable) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String logEntry = String.format("[%s] [%s] [%s] %s", 
                                           timestamp, level, component, message);
            
            pw.println(logEntry);
            
            if (throwable != null) {
                pw.println("Exception details:");
                throwable.printStackTrace(pw);
                pw.println("--- End of exception ---");
            }
            
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Logs application startup.
     */
    public void logApplicationStart() {
        writeLog("INFO", "APPLICATION", "BookNova application started", null);
        writeLog("INFO", "APPLICATION", "Java version: " + System.getProperty("java.version"), null);
        writeLog("INFO", "APPLICATION", "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"), null);
    }
    
    /**
     * Logs application shutdown.
     */
    public void logApplicationShutdown() {
        writeLog("INFO", "APPLICATION", "BookNova application shutdown", null);
    }
    
    /**
     * Logs database connection events.
     */
    public void logDatabaseConnection(boolean success) {
        if (success) {
            writeLog("INFO", "DATABASE", "Database connection established successfully", null);
        } else {
            writeLog("ERROR", "DATABASE", "Failed to establish database connection", null);
        }
    }
    
    /**
     * Logs CRUD operations.
     * 
     * @param entity the entity being operated on
     * @param operation the CRUD operation (CREATE, READ, UPDATE, DELETE)
     * @param success whether the operation was successful
     */
    public void logCrudOperation(String entity, String operation, boolean success) {
        String level = success ? "SUCCESS" : "ERROR";
        String message = String.format("%s operation on %s: %s", 
                                      operation, entity, success ? "SUCCESS" : "FAILED");
        writeLog(level, "REPOSITORY", message, null);
    }
}