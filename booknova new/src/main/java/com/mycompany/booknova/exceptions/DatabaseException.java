package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when database operations fail.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class DatabaseException extends LibroNovaException {
    
    public DatabaseException(String message) {
        super("Database error: " + message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super("Database error: " + message, cause);
    }
}