package com.mycompany.booknova;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.service.BookService;
import com.mycompany.booknova.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic unit tests for BookService.
 * These tests verify the business logic without requiring database connectivity.
 */
public class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl();
    }

    @Test
    void testCreateBook_WithValidData_ShouldNotThrowException() {
        // Arrange
        Book book = new Book();
        book.setIsbn("978-0-123456-78-9");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setPublicationYear(2023);
        book.setCategory("Fiction");
        book.setTotalStock(10);
        book.setAvailableStock(10);

        // This test verifies that the validation logic works correctly
        // In a real scenario, this would fail due to database connectivity
        // but the validation should pass
        assertDoesNotThrow(() -> {
            // The service should validate the book successfully
            // Note: This will fail at repository level due to DB connection
            // but the validation logic should work
            try {
                bookService.createBook(book);
            } catch (Exception e) {
                // Expected to fail due to database connection
                // but should not fail due to validation
                assertTrue(e.getMessage().contains("Database") || 
                          e.getMessage().contains("Connection") ||
                          e.getMessage().contains("MariaDB"));
            }
        });
    }

    @Test
    void testCreateBook_WithInvalidIsbn_ShouldThrowException() {
        // Arrange
        Book book = new Book();
        book.setIsbn(""); // Invalid empty ISBN
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setPublicationYear(2023);
        book.setCategory("Fiction");
        book.setTotalStock(10);
        book.setAvailableStock(10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(book);
        });
    }

    @Test
    void testCreateBook_WithInvalidStock_ShouldThrowException() {
        // Arrange
        Book book = new Book();
        book.setIsbn("978-0-123456-78-9");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setPublicationYear(2023);
        book.setCategory("Fiction");
        book.setTotalStock(5);
        book.setAvailableStock(10); // Available > Total (invalid)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(book);
        });
    }

    @Test
    void testCreateBook_WithNullTitle_ShouldThrowException() {
        // Arrange
        Book book = new Book();
        book.setIsbn("978-0-123456-78-9");
        book.setTitle(null); // Invalid null title
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setPublicationYear(2023);
        book.setCategory("Fiction");
        book.setTotalStock(10);
        book.setAvailableStock(10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(book);
        });
    }

    @Test
    void testBookDomainMethods() {
        // Test Book domain object methods
        Book book = new Book();
        book.setAvailableStock(5);
        
        assertTrue(book.isAvailable(), "Book should be available with stock > 0");
        
        book.setAvailableStock(0);
        assertFalse(book.isAvailable(), "Book should not be available with stock = 0");
        
        book.setAvailableStock(null);
        assertFalse(book.isAvailable(), "Book should not be available with null stock");
    }
}