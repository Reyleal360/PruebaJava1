package com.mycompany.booknova.repository.jdbc;

import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.domain.Loan.LoanStatus;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.infra.config.ConnectionDB;
import com.mycompany.booknova.repository.LoanRepository;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of LoanRepository.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoanRepositoryJdbc implements LoanRepository {
    
    private final ConnectionDB connectionDB;
    private final BookRepositoryJdbc bookRepository;
    private final MemberRepositoryJdbc memberRepository;
    private final UserRepositoryJdbc userRepository;
    
    public LoanRepositoryJdbc() {
        this.connectionDB = ConnectionDB.getInstance();
        this.bookRepository = new BookRepositoryJdbc();
        this.memberRepository = new MemberRepositoryJdbc();
        this.userRepository = new UserRepositoryJdbc();
    }
    
    @Override
    public Loan save(Loan loan) throws DatabaseException {
        String sql = "INSERT INTO loans (member_id, book_id, user_id, loan_date, " +
                     "expected_return_date, actual_return_date, status, penalty, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, loan.getMember().getId());
            stmt.setLong(2, loan.getBook().getId());
            stmt.setLong(3, loan.getUser().getId());
            stmt.setDate(4, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(5, Date.valueOf(loan.getExpectedReturnDate()));
            
            if (loan.getActualReturnDate() != null) {
                stmt.setDate(6, Date.valueOf(loan.getActualReturnDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setString(7, loan.getStatus().name());
            
            if (loan.getPenalty() != null) {
                stmt.setBigDecimal(8, loan.getPenalty());
            } else {
                stmt.setBigDecimal(8, BigDecimal.ZERO);
            }
            
            stmt.setString(9, loan.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating loan failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating loan failed, no ID obtained.");
                }
            }
            
            return loan;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving loan", e);
        }
    }
    
    @Override
    public Loan update(Loan loan) throws DatabaseException {
        String sql = "UPDATE loans SET member_id = ?, book_id = ?, user_id = ?, " +
                     "loan_date = ?, expected_return_date = ?, actual_return_date = ?, " +
                     "status = ?, penalty = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, loan.getMember().getId());
            stmt.setLong(2, loan.getBook().getId());
            stmt.setLong(3, loan.getUser().getId());
            stmt.setDate(4, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(5, Date.valueOf(loan.getExpectedReturnDate()));
            
            if (loan.getActualReturnDate() != null) {
                stmt.setDate(6, Date.valueOf(loan.getActualReturnDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setString(7, loan.getStatus().name());
            stmt.setBigDecimal(8, loan.getPenalty());
            stmt.setString(9, loan.getNotes());
            stmt.setLong(10, loan.getId());
            
            stmt.executeUpdate();
            return loan;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating loan", e);
        }
    }
    
    @Override
    public Optional<Loan> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding loan by ID", e);
        }
    }
    
    @Override
    public List<Loan> findAll() throws DatabaseException {
        String sql = "SELECT * FROM loans ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));}
            
            return loans;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all loans", e);
        }
    }
    
    @Override
    public List<Loan> findActiveLoansByMember(Long memberId) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE member_id = ? AND status = 'ACTIVE' " +
                     "ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding active loans by member", e);
        }
    }
    
    @Override
    public List<Loan> findOverdueLoans() throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE' " +
                     "AND expected_return_date < CURDATE() ORDER BY expected_return_date";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding overdue loans", e);
        }
    }
    
    @Override
    public List<Loan> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE loan_date BETWEEN ? AND ? " +
                     "ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding loans by date range", e);
        }
    }
    
    @Override
    public int countActiveLoansByMember(Long memberId) throws DatabaseException {
        String sql = "SELECT COUNT(*) as count FROM loans WHERE member_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting active loans by member", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Loan object.
     * 
     * @param rs the ResultSet
     * @return the mapped Loan
     * @throws SQLException if database error occurs
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        
        try {
            loan.setId(rs.getLong("id"));
            
            // Load related entities
            Long memberId = rs.getLong("member_id");
            Long bookId = rs.getLong("book_id");
            Long userId = rs.getLong("user_id");
            
            loan.setMember(memberRepository.findById(memberId).orElse(null));
            loan.setBook(bookRepository.findById(bookId).orElse(null));
            loan.setUser(userRepository.findById(userId).orElse(null));
            
            Date loanDate = rs.getDate("loan_date");
            if (loanDate != null) {
                loan.setLoanDate(loanDate.toLocalDate());
            }
            
            Date expectedReturnDate = rs.getDate("expected_return_date");
            if (expectedReturnDate != null) {
                loan.setExpectedReturnDate(expectedReturnDate.toLocalDate());
            }
            
            Date actualReturnDate = rs.getDate("actual_return_date");
            if (actualReturnDate != null) {
                loan.setActualReturnDate(actualReturnDate.toLocalDate());
            }
            
            loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
            loan.setPenalty(rs.getBigDecimal("penalty"));
            loan.setNotes(rs.getString("notes"));
            
        } catch (DatabaseException e) {
            throw new SQLException("Error mapping loan entity relationships", e);
        }
        
        return loan;
    }
}