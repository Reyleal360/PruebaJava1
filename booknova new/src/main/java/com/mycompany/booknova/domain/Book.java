package com.mycompany.booknova.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a Book in the library system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class Book {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String category;
    private Integer availableStock;
    private Integer totalStock;
    
    public Book() {
    }
    
    public Book(Long id, String isbn, String title, String author, 
                String publisher, Integer publicationYear, String category,
                Integer availableStock, Integer totalStock) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.category = category;
        this.availableStock = availableStock;
        this.totalStock = totalStock;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { 
        this.publicationYear = publicationYear; 
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { 
        this.availableStock = availableStock; 
    }
    
    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    
    /**
     * Checks if the book is available for loan.
     * 
     * @return true if stock is available, false otherwise
     */
    public boolean isAvailable() {
        return availableStock != null && availableStock > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", availableStock=" + availableStock +
                '}';
    }
}