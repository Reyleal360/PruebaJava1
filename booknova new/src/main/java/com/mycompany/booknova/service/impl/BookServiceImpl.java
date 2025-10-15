package com.mycompany.booknova.service.impl;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.exceptions.*;
import com.mycompany.booknova.repository.BookRepository;
import com.mycompany.booknova.repository.jdbc.BookRepositoryJdbc;
import com.mycompany.booknova.service.BookService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BookService.
 * Handles book business logic and validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    
    public BookServiceImpl() {
        this.bookRepository = new BookRepositoryJdbc();
    }
    
    @Override
    public Book createBook(Book book) throws DuplicateIsbnException, DatabaseException {
        validateBook(book);
        return bookRepository.save(book);
    }
    
    @Override
    public Book updateBook(Book book) throws BookNotFoundException, DatabaseException {
        validateBook(book);
        
        // Verify book exists
        bookRepository.findById(book.getId())
                .orElseThrow(() -> new BookNotFoundException(book.getId()));
        
        return bookRepository.update(book);
    }
    
    @Override
    public void deleteBook(Long id) throws BookNotFoundException, DatabaseException {
        // Verify book exists
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        
        bookRepository.delete(id);
    }
    
    @Override
    public Book findBookById(Long id) throws BookNotFoundException, DatabaseException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
    
    @Override
    public Book findBookByIsbn(String isbn) throws BookNotFoundException, DatabaseException {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
    
    @Override
    public List<Book> getAllBooks() throws DatabaseException {
        return bookRepository.findAll();
    }
    
    @Override
    public List<Book> getBooksByCategory(String category) throws DatabaseException {
        return bookRepository.findByCategory(category);
    }
    
    @Override
    public List<Book> getBooksByAuthor(String author) throws DatabaseException {
        return bookRepository.findByAuthor(author);
    }
    
    @Override
    public boolean hasAvailableStock(Long bookId) throws BookNotFoundException, DatabaseException {
        Book book = findBookById(bookId);
        return book.isAvailable();
    }
    
    @Override
    public List<Book> getAvailableBooks() throws DatabaseException {
        return bookRepository.findAll().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * Validates book data.
     * 
     * @param book the book to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }
        if (book.getTotalStock() == null || book.getTotalStock() < 0) {
            throw new IllegalArgumentException("Total stock must be greater than or equal to 0");
        }
        if (book.getAvailableStock() == null || book.getAvailableStock() < 0) {
            throw new IllegalArgumentException("Available stock must be greater than or equal to 0");
        }
        if (book.getAvailableStock() > book.getTotalStock()) {
            throw new IllegalArgumentException("Available stock cannot exceed total stock");
        }
    }
}