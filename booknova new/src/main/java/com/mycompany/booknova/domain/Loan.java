/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.booknova.domain;

/**
 *
 * @author Coder
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entity representing a Book Loan.
 * Manages the borrowing and return process with penalties calculation.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class Loan {
    private Long id;
    private Member member;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private LoanStatus status;
    private BigDecimal penalty;
    private String notes;
    
    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        OVERDUE,
        RENEWED
    }
    
    public Loan() {
    }
    
    public Loan(Long id, Member member, Book book, User user,
                LocalDate loanDate, LocalDate expectedReturnDate,
                LocalDate actualReturnDate, LoanStatus status,
                BigDecimal penalty, String notes) {
        this.id = id;
        this.member = member;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.penalty = penalty;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { 
        this.loanDate = loanDate; 
    }
    
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { 
        this.expectedReturnDate = expectedReturnDate; 
    }
    
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { 
        this.actualReturnDate = actualReturnDate; }
    
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    
    public BigDecimal getPenalty() { return penalty; }
    public void setPenalty(BigDecimal penalty) { this.penalty = penalty; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    /**
     * Calculates the penalty amount based on overdue days.
     * Formula: penaltyPerDay * days overdue
     * 
     * @param penaltyPerDay penalty amount per day
     * @return calculated penalty amount
     */
    public BigDecimal calculatePenalty(BigDecimal penaltyPerDay) {
        if (expectedReturnDate == null) {
            return BigDecimal.ZERO;
        }
        
        LocalDate comparisonDate = actualReturnDate != null ? 
                                   actualReturnDate : LocalDate.now();
        
        long overdueDays = ChronoUnit.DAYS.between(expectedReturnDate, comparisonDate);
        
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        
        return penaltyPerDay.multiply(BigDecimal.valueOf(overdueDays));
    }
    
    /**
     * Checks if the loan is overdue.
     * 
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        return expectedReturnDate != null && 
               LocalDate.now().isAfter(expectedReturnDate) &&
               status == LoanStatus.ACTIVE;
    }
    
    /**
     * Gets the number of days overdue.
     * 
     * @return number of days overdue, 0 if not overdue
     */
    public long getOverdueDays() {
        if (expectedReturnDate == null || !isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(expectedReturnDate, LocalDate.now());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}