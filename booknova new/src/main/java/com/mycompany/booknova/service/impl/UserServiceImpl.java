package com.mycompany.booknova.service.impl;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.exceptions.*;
import com.mycompany.booknova.repository.UserRepository;
import com.mycompany.booknova.repository.jdbc.UserRepositoryJdbc;
import com.mycompany.booknova.service.UserService;
import java.util.List;

/**
 * Implementation of UserService.
 * Handles user business logic, authentication and validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    public UserServiceImpl() {
        this.userRepository = new UserRepositoryJdbc();
    }
    
    @Override
    public User createUser(User user) throws DatabaseException {
        validateUser(user);
        
        // Set active by default
        if (user.getActive() == null) {
            user.setActive(true);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) throws UserNotFoundException, DatabaseException {
        validateUser(user);
        
        // Verify user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getUsername()));
        
        return userRepository.update(user);
    }
    
    @Override
    public void deleteUser(Long id) throws UserNotFoundException, DatabaseException {
        // Verify user exists
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ID: " + id));
        
        userRepository.delete(id);
    }
    
    @Override
    public User findUserById(Long id) throws UserNotFoundException, DatabaseException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ID: " + id));
    }
    
    @Override
    public User findUserByUsername(String username) throws UserNotFoundException, DatabaseException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
    
    @Override
    public List<User> getAllUsers() throws DatabaseException {
        return userRepository.findAll();
    }
    
    @Override
    public User login(String username, String password) throws InvalidCredentialsException, DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidCredentialsException();
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException();
        }
        
        return userRepository.validateCredentials(username, password)
                .orElseThrow(() -> new InvalidCredentialsException());
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) 
            throws UserNotFoundException, InvalidCredentialsException, DatabaseException {
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        User user = findUserById(userId);
        
        // Validate old password
        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException();
        }
        
        // Update password
        user.setPassword(newPassword);
        userRepository.update(user);
    }
    
    /**
     * Validates user data.
     * 
     * @param user the user to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role is required");
        }
    }
}