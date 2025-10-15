package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when authentication fails.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class InvalidCredentialsException extends LibroNovaException {
    
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}