/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.booknova.exceptions;

/**
 * Exception thrown when a book is not found in the system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class BookNotFoundException extends LibroNovaException {
    
    public BookNotFoundException(String isbn) {
        super("Book not found with ISBN: " + isbn);
    }
    
    public BookNotFoundException(Long id) {
        super("Book not found with ID: " + id);
    }
}