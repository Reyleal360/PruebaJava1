package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when ISBN already exists in the system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class DuplicateIsbnException extends LibroNovaException {
    
    public DuplicateIsbnException(String isbn) {
        super("ISBN already exists in the system: " + isbn);
    }
}