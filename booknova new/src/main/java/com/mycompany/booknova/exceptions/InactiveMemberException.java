package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a member is inactive.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class InactiveMemberException extends LibroNovaException {
    
    public InactiveMemberException(String memberNumber) {
        super("Member is inactive and cannot borrow books: " + memberNumber);
    }
}