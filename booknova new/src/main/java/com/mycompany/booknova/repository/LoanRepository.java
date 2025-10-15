package com.mycompany.booknova.repository;

import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.exceptions.DatabaseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Loan entity.
 * Defines CRUD operations for loans.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface LoanRepository {
    
    /**
     * Saves a new loan in the database.
     * 
     * @param loan the loan to save
     * @return the saved loan with generated ID
     * @throws DatabaseException if database error occurs
     */
    Loan save(Loan loan) throws DatabaseException;
    
    /**
     * Updates an existing loan.
     * 
     * @param loan the loan to update
     * @return the updated loan
     * @throws DatabaseException if database error occurs
     */
    Loan update(Loan loan) throws DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return Optional containing the loan if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Loan> findById(Long id) throws DatabaseException;
    
    /**
     * Finds all loans.
     * 
     * @return list of all loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> findAll() throws DatabaseException;
    
    /**
     * Finds all active loans for a member.
     * 
     * @param memberId the member ID
     * @return list of active loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> findActiveLoansByMember(Long memberId) throws DatabaseException;
    
    /**
     * Finds all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database error occurs
     */
    List<Loan> findOverdueLoans() throws DatabaseException;
    
    /**
     * Finds loans by date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of loans in the date range
     * @throws DatabaseException if database error occurs
     */
    List<Loan> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    
    /**
     * Counts active loans for a member.
     * 
     * @param memberId the member ID
     * @return number of active loans
     * @throws DatabaseException if database error occurs
     */
    int countActiveLoansByMember(Long memberId) throws DatabaseException;
}