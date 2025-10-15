package com.mycompany.booknova.service.reports;

import com.mycompany.booknova.exceptions.DatabaseException;
import java.io.IOException;

/**
 * Service interface for generating and exporting reports.
 * Handles CSV export functionality for various business reports.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface ReportService {
    
    /**
     * Exports the complete book catalog to a CSV file.
     * 
     * @param filePath the path where the CSV file will be saved
     * @return true if export was successful, false otherwise
     * @throws IOException if file writing fails
     * @throws DatabaseException if database error occurs
     */
    boolean exportBookCatalogToCsv(String filePath) throws IOException, DatabaseException;
    
    /**
     * Exports overdue loans to a CSV file.
     * 
     * @param filePath the path where the CSV file will be saved
     * @return true if export was successful, false otherwise
     * @throws IOException if file writing fails
     * @throws DatabaseException if database error occurs
     */
    boolean exportOverdueLoansToCSv(String filePath) throws IOException, DatabaseException;
    
    /**
     * Exports all active loans to a CSV file.
     * 
     * @param filePath the path where the CSV file will be saved
     * @return true if export was successful, false otherwise
     * @throws IOException if file writing fails
     * @throws DatabaseException if database error occurs
     */
    boolean exportActiveLoansToCSv(String filePath) throws IOException, DatabaseException;
    
    /**
     * Exports members list to a CSV file.
     * 
     * @param filePath the path where the CSV file will be saved
     * @return true if export was successful, false otherwise
     * @throws IOException if file writing fails
     * @throws DatabaseException if database error occurs
     */
    boolean exportMembersToCSv(String filePath) throws IOException, DatabaseException;
}