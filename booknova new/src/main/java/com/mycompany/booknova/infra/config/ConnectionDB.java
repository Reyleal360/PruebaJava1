package com.mycompany.booknova.infra.config;

import com.mycompany.booknova.infra.logging.AppLogger;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for managing database connections.
 * Provides a centralized connection pool management for the LibroNova application.
 * 
 * @author LibroNova Development Team
 * @version 1.0
 * @since 2025-10-14
 */
public class ConnectionDB {
    
    private static final Logger LOGGER = Logger.getLogger(ConnectionDB.class.getName());
    private final AppLogger appLogger = AppLogger.getInstance();
    
    // Singleton instance
    private static ConnectionDB instance;
    
    // Database connection parameters
    private String url;
    private String user;
    private String password;
    private String driver;
    
    /**
     * Private constructor to prevent external instantiation.
     * Loads database configuration from config.properties file.
     */
    private ConnectionDB() {
        loadConfiguration();
    }
    
    /**
     * Gets the singleton instance of ConnectionDB.
     * Thread-safe implementation using synchronized block.
     * 
     * @return the unique instance of ConnectionDB
     */
    public static synchronized ConnectionDB getInstance() {
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }
    
    /**
     * Loads database configuration from config.properties file.
     * If the file is not found, uses default configuration.
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                LOGGER.log(Level.WARNING, 
                    "config.properties not found. Using default configuration.");
                loadDefaultConfiguration();
                return;
            }
            
            properties.load(input);
            
            this.driver = properties.getProperty("db.driver", "org.mariadb.jdbc.Driver");
            this.url = properties.getProperty("db.url", 
                "jdbc:mariadb://localhost:3306/libronova?useSSL=false&serverTimezone=UTC");
            this.user = properties.getProperty("db.user", "root");
            this.password = properties.getProperty("db.password", "Qwe.123*");
            
            // Load JDBC driver
            Class.forName(this.driver);
            
            LOGGER.log(Level.INFO, "Database configuration loaded successfully.");
            appLogger.logInfo("CONNECTION_DB", "Database configuration loaded successfully");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading config.properties file.", e);
            loadDefaultConfiguration();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MariaDB JDBC Driver not found.", e);
        }
    }
    
    /**
     * Loads default database configuration.
     * Used as fallback when config.properties is not available.
     */
    private void loadDefaultConfiguration() {
        this.driver = "org.mariadb.jdbc.Driver";
        this.url = "jdbc:mariadb://localhost:3306/libronova?useSSL=false&serverTimezone=UTC";
        this.user = "root";
        this.password = "1234";
        
        try {
            Class.forName(this.driver);
            LOGGER.log(Level.INFO, "Default database configuration loaded.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MariaDB JDBC Driver not found with default config.", e);
        }
    }
    
    /**
     * Establishes and returns a new database connection.
     * This method should be used with try-with-resources to ensure proper closure.
     * 
     * @return a new Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            LOGGER.log(Level.FINE, "Database connection established successfully.");
            appLogger.logDatabaseConnection(true);
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error establishing database connection.", e);
            appLogger.logDatabaseConnection(false);
            appLogger.logError("CONNECTION_DB", "Failed to establish database connection: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tests the database connection.
     * Useful for application startup validation.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection.isValid(5);
            if (isValid) {
                LOGGER.log(Level.INFO, "Database connection test: SUCCESS");
            } else {
                LOGGER.log(Level.WARNING, "Database connection test: FAILED");
            }
            return isValid;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed.", e);
            return false;
        }
    }
    
    /**
     * Gets the database URL.
     * 
     * @return the database connection URL
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Gets the database user.
     * 
     * @return the database username
     */
    public String getUser() {
        return user;
    }
    
    public static void main(String[] args) {
    ConnectionDB db = ConnectionDB.getInstance();
    if (db.testConnection()) {
        System.out.println("✅ Conexión exitosa!");
    } else {
        System.out.println("❌ Error de conexión!");
    }
}
}