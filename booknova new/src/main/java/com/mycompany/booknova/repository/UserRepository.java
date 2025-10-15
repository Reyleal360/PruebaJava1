package com.mycompany.booknova.repository;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Defines CRUD operations for users.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface UserRepository {
    
    /**
     * Saves a new user in the database.
     * 
     * @param user the user to save
     * @return the saved user with generated ID
     * @throws DatabaseException if database error occurs
     */
    User save(User user) throws DatabaseException;
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update
     * @return the updated user
     * @throws DatabaseException if database error occurs
     */
    User update(User user) throws DatabaseException;
    
    /**
     * Deletes a user by ID.
     * 
     * @param id the user ID
     * @throws DatabaseException if database error occurs
     */
    void delete(Long id) throws DatabaseException;
    
    /**
     * Finds a user by ID.
     * 
     * @param id the user ID
     * @return Optional containing the user if found
     * @throws DatabaseException if database error occurs
     */
    Optional<User> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return Optional containing the user if found
     * @throws DatabaseException if database error occurs
     */
    Optional<User> findByUsername(String username) throws DatabaseException;
    
    /**
     * Finds all users.
     * 
     * @return list of all users
     * @throws DatabaseException if database error occurs
     */
    List<User> findAll() throws DatabaseException;
    
    /**
     * Validates user credentials.
     * 
     * @param username the username
     * @param password the password
     * @return Optional containing the user if credentials are valid
     * @throws DatabaseException if database error occurs
     */
    Optional<User> validateCredentials(String username, String password) throws DatabaseException;
}