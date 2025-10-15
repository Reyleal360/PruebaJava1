package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a book is not available for loan.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class BookNotAvailableException extends LibroNovaException {
    
    public BookNotAvailableException(String isbn) {
        super("Book is not available for loan. ISBN: " + isbn);
    }
}