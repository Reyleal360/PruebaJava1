package com.mycompany.booknova.service.impl;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.domain.Loan.LoanStatus;
import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.exceptions.*;
import com.mycompany.booknova.infra.config.ConnectionDB;
import com.mycompany.booknova.repository.BookRepository;
import com.mycompany.booknova.repository.LoanRepository;
import com.mycompany.booknova.repository.MemberRepository;
import com.mycompany.booknova.repository.UserRepository;
import com.mycompany.booknova.repository.jdbc.BookRepositoryJdbc;
import com.mycompany.booknova.repository.jdbc.LoanRepositoryJdbc;
import com.mycompany.booknova.repository.jdbc.MemberRepositoryJdbc;
import com.mycompany.booknova.repository.jdbc.UserRepositoryJdbc;
import com.mycompany.booknova.service.LoanService;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of LoanService.
 * Handles loan business logic with transactions and validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoanServiceImpl implements LoanService {
    
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ConnectionDB connectionDB;
    
    private int maxLoanDays;
    private BigDecimal penaltyPerDay;
    
    public LoanServiceImpl() {
        this.loanRepository = new LoanRepositoryJdbc();
        this.bookRepository = new BookRepositoryJdbc();
        this.memberRepository = new MemberRepositoryJdbc();
        this.userRepository = new UserRepositoryJdbc();
        this.connectionDB = ConnectionDB.getInstance();
        
        loadBusinessConfiguration();
    }
    
    /**
     * Loads business configuration from config.properties.
     */
    private void loadBusinessConfiguration() {
        Properties properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input != null) {
                properties.load(input);
                this.maxLoanDays = Integer.parseInt(
                    properties.getProperty("max.loan.days", "15"));
                this.penaltyPerDay = new BigDecimal(
                    properties.getProperty("late.fee.per.day", "1.50"));
            } else {
                // Default values
                this.maxLoanDays = 15;
                this.penaltyPerDay = new BigDecimal("1.50");
            }
            
        } catch (IOException | NumberFormatException e) {
            // Use default values
            this.maxLoanDays = 15;
            this.penaltyPerDay = new BigDecimal("1.50");
        }
    }
    
    @Override
    public Loan createLoan(Long memberId, Long bookId, Long userId) 
            throws MemberNotFoundException, BookNotFoundException, UserNotFoundException,
                   InactiveMemberException, BookNotAvailableException, 
                   LoanLimitExceededException, DatabaseException {
        
        // Validate entities exist
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID: " + userId));
        
        // Business validations
        if (!member.isActive()) {
            throw new InactiveMemberException(member.getMemberNumber());
        }
        
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getIsbn());
        }
        
        // Check loan limit
        int activeLoans = loanRepository.countActiveLoansByMember(memberId);
        int maxLoans = member.getMembershipType().getMaxLoans();
        
        if (activeLoans >= maxLoans) {
            throw new LoanLimitExceededException(member.getMemberNumber(), maxLoans);
        }
        
        // Create loan with transaction
        Connection conn = null;
        try {
            conn = connectionDB.getConnection();
            conn.setAutoCommit(false);
            
            // Create loan
            Loan loan = new Loan();
            loan.setMember(member);
            loan.setBook(book);
            loan.setUser(user);
            loan.setLoanDate(LocalDate.now());
            loan.setExpectedReturnDate(LocalDate.now().plusDays(maxLoanDays));
            loan.setStatus(LoanStatus.ACTIVE);
            loan.setPenalty(BigDecimal.ZERO);
            
            Loan savedLoan = loanRepository.save(loan);
            
            // Update book stock
            book.setAvailableStock(book.getAvailableStock() - 1);
            bookRepository.updateAvailableStock(book.getId(), book.getAvailableStock());
            
            conn.commit();
            return savedLoan;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Error rolling back transaction", ex);
                }
            }
            throw new DatabaseException("Error creating loan", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new DatabaseException("Error closing connection", e);
                }
            }
        }
    }
    
    @Override
    public Loan returnBook(Long loanId) throws LoanNotFoundException, DatabaseException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active");
        }
        
        Connection conn = null;
        try {
            conn = connectionDB.getConnection();
            conn.setAutoCommit(false);
            
            // Set return date
            loan.setActualReturnDate(LocalDate.now());
            
            // Calculate penalty if overdue
            if (loan.isOverdue()) {
                BigDecimal penalty = loan.calculatePenalty(penaltyPerDay);
                loan.setPenalty(penalty);
                loan.setStatus(LoanStatus.OVERDUE);
            } else {
                loan.setStatus(LoanStatus.RETURNED);
            }
            
            // Update loan
            Loan updatedLoan = loanRepository.update(loan);
            
            // Update book stock
            Book book = loan.getBook();
            book.setAvailableStock(book.getAvailableStock() + 1);
            bookRepository.updateAvailableStock(book.getId(), book.getAvailableStock());
            
            conn.commit();
            return updatedLoan;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Error rolling back transaction", ex);
                }
            }
            throw new DatabaseException("Error returning book", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new DatabaseException("Error closing connection", e);
                }
            }
        }
    }
    
    @Override
    public Loan findLoanById(Long id) throws LoanNotFoundException, DatabaseException {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }
    
    @Override
    public List<Loan> getAllLoans() throws DatabaseException {
        return loanRepository.findAll();
    }
    
    @Override
    public List<Loan> getActiveLoansByMember(Long memberId) throws DatabaseException {
        return loanRepository.findActiveLoansByMember(memberId);
    }
    
    @Override
    public List<Loan> getOverdueLoans() throws DatabaseException {
        return loanRepository.findOverdueLoans();
    }
    
    @Override
    public List<Loan> getLoansByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return loanRepository.findByDateRange(startDate, endDate);
    }
    
    @Override
    public BigDecimal calculatePenalty(Long loanId) throws LoanNotFoundException, DatabaseException {
        Loan loan = findLoanById(loanId);
        return loan.calculatePenalty(penaltyPerDay);
    }
    
    @Override
    public Loan renewLoan(Long loanId, int additionalDays) throws LoanNotFoundException, DatabaseException {
        Loan loan = findLoanById(loanId);
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Only active loans can be renewed");
        }
        
        if (loan.isOverdue()) {
            throw new IllegalStateException("Overdue loans cannot be renewed");
        }
        
        // Extend expected return date
        loan.setExpectedReturnDate(loan.getExpectedReturnDate().plusDays(additionalDays));
        loan.setStatus(LoanStatus.RENEWED);
        
        return loanRepository.update(loan);
    }
}