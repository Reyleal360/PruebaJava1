package com.mycompany.booknova.service;

import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.exceptions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Loan business logic.
 * Handles loan operations with business validations and transactions.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface LoanService {
    
    /**
     * Creates a new loan (borrows a book).
     * Validates member status, book availability, and loan limits.
     * Uses transaction to update book stock.
     * 
     * @param memberId the member ID
     * @param bookId the book ID
     * @param userId the user ID who registers the loan
     * @return the created loan
     * @throws MemberNotFoundException if member not found
     * @throws BookNotFoundException if book not found
     * @throws UserNotFoundException if user not found
     * @throws InactiveMemberException if member is inactive
     * @throws BookNotAvailableException if book has no stock
     * @throws LoanLimitExceededException if member exceeded loan limit
     * @throws DatabaseException if database error occurs
     */
    Loan createLoan(Long memberId, Long bookId, Long userId) 
            throws MemberNotFoundException, BookNotFoundException, UserNotFoundException,
                   InactiveMemberException, BookNotAvailableException, 
                   LoanLimitExceededException, DatabaseException;
    
    /**
     * Processes the return of a borrowed book.
     * Calculates penalties if overdue.
     * Uses transaction to update book stock and loan status.
     * 
     * @param loanId the loan ID
     * @return the updated loan with penalty information
     * @throws LoanNotFoundException if loan not found
     * @throws DatabaseException if database error occurs
     */
    Loan returnBook(Long loanId) throws LoanNotFoundException, DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return the found loan
     * @throws LoanNotFoundException if loan not found
     * @throws DatabaseException if database error occurs
     */
    Loan findLoanById(Long id) throws LoanNotFoundException, DatabaseException;
    
    /**
     * Retrieves all loans.
     * 
     * @return list of all loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> getAllLoans() throws DatabaseException;
    
    /**
     * Gets all active loans for a specific member.
     * 
     * @param memberId the member ID
     * @return list of active loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> getActiveLoansByMember(Long memberId) throws DatabaseException;
    
    /**
     * Gets all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> getOverdueLoans() throws DatabaseException;
    
    /**
     * Gets loans within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of loans in the date range
     * @throws DatabaseException if database error occurs
     */
    List<Loan> getLoansByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    
    /**
     * Calculates the penalty for a loan.
     * 
     * @param loanId the loan ID
     * @return the calculated penalty amount
     * @throws LoanNotFoundException if loan not found
     * @throws DatabaseException if database error occurs
     */
    BigDecimal calculatePenalty(Long loanId) throws LoanNotFoundException, DatabaseException;
    
    /**
     * Renews a loan (extends the return date).
     * 
     * @param loanId the loan ID
     * @param additionalDays days to add to expected return date
     * @return the renewed loan
     * @throws LoanNotFoundException if loan not found
     * @throws DatabaseException if database error occurs
     */
    Loan renewLoan(Long loanId, int additionalDays) throws LoanNotFoundException, DatabaseException;
}