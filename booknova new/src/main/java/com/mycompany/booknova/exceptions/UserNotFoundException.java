package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a user is not found in the system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class UserNotFoundException extends LibroNovaException {
    
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}