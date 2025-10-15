package com.mycompany.booknova.service;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.exceptions.*;
import java.util.List;

/**
 * Service interface for Book business logic.
 * Handles book management operations with business validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface BookService {
    
    /**
     * Creates a new book in the system.
     * Validates that ISBN is unique.
     * 
     * @param book the book to create
     * @return the created book
     * @throws DuplicateIsbnException if ISBN already exists
     * @throws DatabaseException if database error occurs
     */
    Book createBook(Book book) throws DuplicateIsbnException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param book the book to update
     * @return the updated book
     * @throws BookNotFoundException if book not found
     * @throws DatabaseException if database error occurs
     */
    Book updateBook(Book book) throws BookNotFoundException, DatabaseException;
    
    /**
     * Deletes a book by ID.
     * 
     * @param id the book ID
     * @throws BookNotFoundException if book not found
     * @throws DatabaseException if database error occurs
     */
    void deleteBook(Long id) throws BookNotFoundException, DatabaseException;
    
    /**
     * Finds a book by ID.
     * 
     * @param id the book ID
     * @return the found book
     * @throws BookNotFoundException if book not found
     * @throws DatabaseException if database error occurs
     */
    Book findBookById(Long id) throws BookNotFoundException, DatabaseException;
    
    /**
     * Finds a book by ISBN.
     * 
     * @param isbn the book ISBN
     * @return the found book
     * @throws BookNotFoundException if book not found
     * @throws DatabaseException if database error occurs
     */
    Book findBookByIsbn(String isbn) throws BookNotFoundException, DatabaseException;
    
    /**
     * Retrieves all books.
     * 
     * @return list of all books
     * @throws DatabaseException if database error occurs
     */
    List<Book> getAllBooks() throws DatabaseException;
    
    /**
     * Finds books by category.
     * 
     * @param category the book category
     * @return list of books in the category
     * @throws DatabaseException if database error occurs
     */
    List<Book> getBooksByCategory(String category) throws DatabaseException;
    
    /**
     * Finds books by author.
     * 
     * @param author the book author
     * @return list of books by the author
     * @throws DatabaseException if database error occurs
     */
    List<Book> getBooksByAuthor(String author) throws DatabaseException;
    
    /**
     * Validates if a book has available stock.
     * 
     * @param bookId the book ID
     * @return true if book has available stock
     * @throws BookNotFoundException if book not found
     * @throws DatabaseException if database error occurs
     */
    boolean hasAvailableStock(Long bookId) throws BookNotFoundException, DatabaseException;
    
    /**
     * Gets available books (stock > 0).
     * 
     * @return list of available books
     * @throws DatabaseException if database error occurs
     */
    List<Book> getAvailableBooks() throws DatabaseException;
}