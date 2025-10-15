/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.booknova.exceptions;

/**
 *
 * @author Coder
 */

/**
 * Base exception for all LibroNova business logic exceptions.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LibroNovaException extends Exception {
    
    public LibroNovaException(String message) {
        super(message);
    }
    
    public LibroNovaException(String message, Throwable cause) {
        super(message, cause);
    }
}