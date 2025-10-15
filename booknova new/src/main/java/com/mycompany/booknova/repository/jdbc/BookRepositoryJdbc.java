package com.mycompany.booknova.repository.jdbc;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.exceptions.DuplicateIsbnException;
import com.mycompany.booknova.infra.config.ConnectionDB;
import com.mycompany.booknova.repository.BookRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of BookRepository.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class BookRepositoryJdbc implements BookRepository {
    
    private final ConnectionDB connectionDB;
    
    public BookRepositoryJdbc() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Book save(Book book) throws DuplicateIsbnException, DatabaseException {
        // Check if ISBN already exists
        if (findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateIsbnException(book.getIsbn());
        }
        
        String sql = "INSERT INTO books (isbn, title, author, publisher, publication_year, " +
                     "category, available_stock, total_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setString(6, book.getCategory());
            stmt.setInt(7, book.getAvailableStock());
            stmt.setInt(8, book.getTotalStock());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Creating book failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating book failed, no ID obtained.");
                }
            }
            
            return book;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving book", e);
        }
    }
    
    @Override
    public Book update(Book book) throws DatabaseException {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, " +
                     "publication_year = ?, category = ?, available_stock = ?, total_stock = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setString(6, book.getCategory());
            stmt.setInt(7, book.getAvailableStock());
            stmt.setInt(8, book.getTotalStock());
            stmt.setLong(9, book.getId());
            
            stmt.executeUpdate();
            return book;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating book", e);
        }
    }
    
    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting book", e);
        }
    }
    
    @Override
    public Optional<Book> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding book by ID", e);
        }
    }
    
    @Override
    public Optional<Book> findByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding book by ISBN", e);
        }
    }
    
    @Override
    public List<Book> findAll() throws DatabaseException {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            
            return books;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all books", e);
        }
    }
    
    @Override
    public List<Book> findByCategory(String category) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE category = ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
            return books;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding books by category", e);
        }
    }
    
    @Override
    public List<Book> findByAuthor(String author) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + author + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
            return books;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding books by author", e);
        }
    }
    
    @Override
    public void updateAvailableStock(Long bookId, Integer newStock) throws DatabaseException {
        String sql = "UPDATE books SET available_stock = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newStock);
            stmt.setLong(2, bookId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating book stock", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Book object.
     * 
     * @param rs the ResultSet
     * @return the mapped Book
     * @throws SQLException if database error occurs
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setCategory(rs.getString("category"));
        book.setAvailableStock(rs.getInt("available_stock"));
        book.setTotalStock(rs.getInt("total_stock"));
        return book;
    }
}