package com.mycompany.booknova.repository;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.exceptions.DuplicateIsbnException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity.
 * Defines CRUD operations for books.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface BookRepository {
    
    /**
     * Saves a new book in the database.
     * 
     * @param book the book to save
     * @return the saved book with generated ID
     * @throws DuplicateIsbnException if ISBN already exists
     * @throws DatabaseException if database error occurs
     */
    Book save(Book book) throws DuplicateIsbnException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param book the book to update
     * @return the updated book
     * @throws DatabaseException if database error occurs
     */
    Book update(Book book) throws DatabaseException;
    
    /**
     * Deletes a book by ID.
     * 
     * @param id the book ID
     * @throws DatabaseException if database error occurs
     */
    void delete(Long id) throws DatabaseException;
    
    /**
     * Finds a book by ID.
     * 
     * @param id the book ID
     * @return Optional containing the book if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Book> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a book by ISBN.
     * 
     * @param isbn the book ISBN
     * @return Optional containing the book if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Book> findByIsbn(String isbn) throws DatabaseException;
    
    /**
     * Finds all books.
     * 
     * @return list of all books
     * @throws DatabaseException if database error occurs
     */
    List<Book> findAll() throws DatabaseException;
    
    /**
     * Finds books by category.
     * 
     * @param category the book category
     * @return list of books in the category
     * @throws DatabaseException if database error occurs
     */
    List<Book> findByCategory(String category) throws DatabaseException;
    
    /**
     * Finds books by author.
     * 
     * @param author the book author
     * @return list of books by the author
     * @throws DatabaseException if database error occurs
     */
    List<Book> findByAuthor(String author) throws DatabaseException;
    
    /**
     * Updates the available stock of a book.
     * 
     * @param bookId the book ID
     * @param newStock the new stock amount
     * @throws DatabaseException if database error occurs
     */
    void updateAvailableStock(Long bookId, Integer newStock) throws DatabaseException;
}