package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a member exceeds the loan limit.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoanLimitExceededException extends LibroNovaException {
    
    public LoanLimitExceededException(String memberNumber, int maxLoans) {
        super("Member " + memberNumber + " has reached the maximum loan limit of " + maxLoans + " books");
    }
}