package com.mycompany.booknova.service.auth;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.domain.User.UserRole;
import com.mycompany.booknova.infra.logging.AppLogger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock authentication service with predefined users.
 * Provides login functionality for ADMINISTRATOR and LIBRARIAN roles.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MockAuthenticationService {
    
    private static MockAuthenticationService instance;
    private final AppLogger logger = AppLogger.getInstance();
    private final List<User> mockUsers;
    private User currentUser;
    
    private MockAuthenticationService() {
        mockUsers = createMockUsers();
        currentUser = null;
    }
    
    public static MockAuthenticationService getInstance() {
        if (instance == null) {
            instance = new MockAuthenticationService();
        }
        return instance;
    }
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the password
     * @return the authenticated user, or null if authentication fails
     */
    public User login(String username, String password) {
        logger.logInfo("AUTHENTICATION", "Login attempt for username: " + username);
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            logger.logError("AUTHENTICATION", "Login failed - Empty credentials");
            return null;
        }
        
        for (User user : mockUsers) {
            if (user.getUsername().equals(username.trim()) && 
                user.getPassword().equals(password) && 
                user.getActive()) {
                
                currentUser = user;
                logger.logSuccess("AUTHENTICATION", "Login successful for user: " + username + " (" + user.getRole() + ")");
                logger.logUserActivity(user.getFullName(), "User logged in successfully");
                return user;
            }
        }
        
        logger.logError("AUTHENTICATION", "Login failed - Invalid credentials for username: " + username);
        return null;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.logUserActivity(currentUser.getFullName(), "User logged out");
            logger.logInfo("AUTHENTICATION", "User logged out: " + currentUser.getUsername());
            currentUser = null;
        }
    }
    
    /**
     * Gets the currently authenticated user.
     * 
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Checks if the current user has administrator privileges.
     * 
     * @return true if current user is administrator, false otherwise
     */
    public boolean isAdministrator() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMINISTRATOR;
    }
    
    /**
     * Checks if the current user has librarian privileges.
     * 
     * @return true if current user is librarian or administrator, false otherwise
     */
    public boolean isLibrarian() {
        return currentUser != null && 
               (currentUser.getRole() == UserRole.LIBRARIAN || 
                currentUser.getRole() == UserRole.ADMINISTRATOR);
    }
    
    /**
     * Gets all available users (for testing purposes).
     * 
     * @return list of mock users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(mockUsers);
    }
    
    /**
     * Creates the predefined mock users.
     * 
     * @return list of mock users
     */
    private List<User> createMockUsers() {
        List<User> users = new ArrayList<>();
        
        // Administrator user
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEmail("admin@libronova.com");
        admin.setRole(UserRole.ADMINISTRATOR);
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        // Librarian user
        User librarian = new User();
        librarian.setId(2L);
        librarian.setUsername("librarian");
        librarian.setPassword("librarian");
        librarian.setFirstName("María");
        librarian.setLastName("Bibliotecaria");
        librarian.setEmail("librarian@libronova.com");
        librarian.setRole(UserRole.LIBRARIAN);
        librarian.setActive(true);
        librarian.setCreatedAt(LocalDateTime.now());
        
        // Additional administrator
        User superAdmin = new User();
        superAdmin.setId(3L);
        superAdmin.setUsername("superadmin");
        superAdmin.setPassword("super123");
        superAdmin.setFirstName("Carlos");
        superAdmin.setLastName("Administrador");
        superAdmin.setEmail("superadmin@libronova.com");
        superAdmin.setRole(UserRole.ADMINISTRATOR);
        superAdmin.setActive(true);
        superAdmin.setCreatedAt(LocalDateTime.now());
        
        // Additional librarian
        User libAsistant = new User();
        libAsistant.setId(4L);
        libAsistant.setUsername("assistant");
        libAsistant.setPassword("assistant");
        libAsistant.setFirstName("Ana");
        libAsistant.setLastName("Asistente");
        libAsistant.setEmail("assistant@libronova.com");
        libAsistant.setRole(UserRole.ASSISTANT);
        libAsistant.setActive(true);
        libAsistant.setCreatedAt(LocalDateTime.now());
        
        users.add(admin);
        users.add(librarian);
        users.add(superAdmin);
        users.add(libAsistant);
        
        logger.logInfo("AUTHENTICATION", "Mock authentication service initialized with " + users.size() + " users");
        
        return users;
    }
}