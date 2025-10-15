package com.mycompany.booknova.service;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.exceptions.*;
import java.util.List;

/**
 * Service interface for User business logic.
 * Handles user management and authentication operations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface UserService {
    
    /**
     * Creates a new user in the system.
     * 
     * @param user the user to create
     * @return the created user
     * @throws DatabaseException if database error occurs
     */
    User createUser(User user) throws DatabaseException;
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update
     * @return the updated user
     * @throws UserNotFoundException if user not found
     * @throws DatabaseException if database error occurs
     */
    User updateUser(User user) throws UserNotFoundException, DatabaseException;
    
    /**
     * Deletes a user by ID.
     * 
     * @param id the user ID
     * @throws UserNotFoundException if user not found
     * @throws DatabaseException if database error occurs
     */
    void deleteUser(Long id) throws UserNotFoundException, DatabaseException;
    
    /**
     * Finds a user by ID.
     * 
     * @param id the user ID
     * @return the found user
     * @throws UserNotFoundException if user not found
     * @throws DatabaseException if database error occurs
     */
    User findUserById(Long id) throws UserNotFoundException, DatabaseException;
    
    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return the found user
     * @throws UserNotFoundException if user not found
     * @throws DatabaseException if database error occurs
     */
    User findUserByUsername(String username) throws UserNotFoundException, DatabaseException;
    
    /**
     * Retrieves all users.
     * 
     * @return list of all users
     * @throws DatabaseException if database error occurs
     */
    List<User> getAllUsers() throws DatabaseException;
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the password
     * @return the authenticated user
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws DatabaseException if database error occurs
     */
    User login(String username, String password) throws InvalidCredentialsException, DatabaseException;
    
    /**
     * Changes a user's password.
     * 
     * @param userId the user ID
     * @param oldPassword the old password
     * @param newPassword the new password
     * @throws UserNotFoundException if user not found
     * @throws InvalidCredentialsException if old password is incorrect
     * @throws DatabaseException if database error occurs
     */
    void changePassword(Long userId, String oldPassword, String newPassword) 
            throws UserNotFoundException, InvalidCredentialsException, DatabaseException;
}