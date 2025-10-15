package com.mycompany.booknova.repository.jdbc;

import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.domain.Member.MembershipType;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.infra.config.ConnectionDB;
import com.mycompany.booknova.repository.MemberRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of MemberRepository.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MemberRepositoryJdbc implements MemberRepository {
    
    private final ConnectionDB connectionDB;
    
    public MemberRepositoryJdbc() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Member save(Member member) throws DatabaseException {
        String sql = "INSERT INTO members (member_number, first_name, last_name, document_id, " +
                     "email, phone, address, registration_date, active, membership_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, member.getMemberNumber());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            stmt.setString(4, member.getDocumentId());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getPhone());
            stmt.setString(7, member.getAddress());
            stmt.setDate(8, Date.valueOf(member.getRegistrationDate()));
            stmt.setBoolean(9, member.getActive());
            stmt.setString(10, member.getMembershipType().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating member failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating member failed, no ID obtained.");
                }
            }
            
            return member;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving member", e);
        }
    }
    
    @Override
    public Member update(Member member) throws DatabaseException {
        String sql = "UPDATE members SET member_number = ?, first_name = ?, last_name = ?, " +
                     "document_id = ?, email = ?, phone = ?, address = ?, " +
                     "registration_date = ?, active = ?, membership_type = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getMemberNumber());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            stmt.setString(4, member.getDocumentId());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getPhone());
            stmt.setString(7, member.getAddress());
            stmt.setDate(8, Date.valueOf(member.getRegistrationDate()));
            stmt.setBoolean(9, member.getActive());
            stmt.setString(10, member.getMembershipType().name());
            stmt.setLong(11, member.getId());
            
            stmt.executeUpdate();
            return member;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating member", e);
        }
    }
    
    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM members WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting member", e);
        }
    }
    
    @Override
    public Optional<Member> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding member by ID", e);
        }
    }
    
    @Override
    public Optional<Member> findByMemberNumber(String memberNumber) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE member_number = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, memberNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding member by member number", e);
        }
    }
    
    @Override
    public Optional<Member> findByDocumentId(String documentId) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE document_id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding member by document ID", e);
        }
    }
    
    @Override
    public List<Member> findAll() throws DatabaseException {
        String sql = "SELECT * FROM members ORDER BY first_name, last_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all members", e);
        }
    }
    
    @Override
    public List<Member> findAllActive() throws DatabaseException {
        String sql = "SELECT * FROM members WHERE active = true ORDER BY first_name, last_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding active members", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Member object.
     * 
     * @param rs the ResultSet
     * @return the mapped Member
     * @throws SQLException if database error occurs
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setMemberNumber(rs.getString("member_number"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setDocumentId(rs.getString("document_id"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));
        
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            member.setRegistrationDate(regDate.toLocalDate());
        }
        
        member.setActive(rs.getBoolean("active"));
        member.setMembershipType(MembershipType.valueOf(rs.getString("membership_type")));
        
        return member;
    }
}