package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a loan is not found in the system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoanNotFoundException extends LibroNovaException {
    
    public LoanNotFoundException(Long id) {
        super("Loan not found with ID: " + id);
    }
}