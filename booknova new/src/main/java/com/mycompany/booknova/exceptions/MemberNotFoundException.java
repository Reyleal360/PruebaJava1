package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a member is not found in the system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MemberNotFoundException extends LibroNovaException {
    
    public MemberNotFoundException(String memberNumber) {
        super("Member not found with number: " + memberNumber);
    }
    
    public MemberNotFoundException(Long id) {
        super("Member not found with ID: " + id);
    }
}