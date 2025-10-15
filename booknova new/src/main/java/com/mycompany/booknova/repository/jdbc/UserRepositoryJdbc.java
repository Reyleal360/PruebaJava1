package com.mycompany.booknova.repository.jdbc;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.domain.User.UserRole;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.infra.config.ConnectionDB;
import com.mycompany.booknova.repository.UserRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserRepository.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class UserRepositoryJdbc implements UserRepository {
    
    private final ConnectionDB connectionDB;
    
    public UserRepositoryJdbc() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public User save(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password, first_name, last_name, " +
                     "email, role, active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().name());
            stmt.setBoolean(7, user.getActive());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user", e);
        }
    }
    
    @Override
    public User update(User user) throws DatabaseException {
        String sql = "UPDATE users SET username = ?, password = ?, first_name = ?, " +
                     "last_name = ?, email = ?, role = ?, active = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().name());
            stmt.setBoolean(7, user.getActive());
            stmt.setLong(8, user.getId());
            
            stmt.executeUpdate();
            return user;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user", e);
        }
    }
    
    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user", e);
        }
    }
    
    @Override
    public Optional<User> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by ID", e);
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by username", e);
        }
    }
    
    @Override
    public List<User> findAll() throws DatabaseException {
        String sql = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            return users;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all users", e);
        }
    }
    
    @Override
    public Optional<User> validateCredentials(String username, String password) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND active = true";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error validating credentials", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a User object.
     * 
     * @param rs the ResultSet
     * @return the mapped User
     * @throws SQLException if database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}