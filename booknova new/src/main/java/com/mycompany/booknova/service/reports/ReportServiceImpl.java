package com.mycompany.booknova.service.reports;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.exceptions.DatabaseException;
import com.mycompany.booknova.infra.logging.AppLogger;
import com.mycompany.booknova.service.BookService;
import com.mycompany.booknova.service.LoanService;
import com.mycompany.booknova.service.MemberService;
import com.mycompany.booknova.service.impl.BookServiceImpl;
import com.mycompany.booknova.service.impl.LoanServiceImpl;
import com.mycompany.booknova.service.impl.MemberServiceImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService for CSV export functionality.
 * Provides methods to export various business reports to CSV format.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class ReportServiceImpl implements ReportService {
    
    private final BookService bookService;
    private final LoanService loanService;
    private final MemberService memberService;
    private final AppLogger logger;
    
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_QUOTE = "\"";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ReportServiceImpl() {
        this.bookService = new BookServiceImpl();
        this.loanService = new LoanServiceImpl();
        this.memberService = new MemberServiceImpl();
        this.logger = AppLogger.getInstance();
    }
    
    @Override
    public boolean exportBookCatalogToCsv(String filePath) throws IOException, DatabaseException {
        logger.logInfo("REPORT_SERVICE", "Starting book catalog CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("ID,ISBN,Title,Author,Publisher,Publication Year,Category,Total Stock,Available Stock,Status\n");
            
            // Get all books
            List<Book> books = bookService.getAllBooks();
            
            // Write book data
            for (Book book : books) {
                writer.append(String.valueOf(book.getId())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(book.getIsbn())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(book.getTitle())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(book.getAuthor())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(book.getPublisher())).append(CSV_SEPARATOR);
                writer.append(String.valueOf(book.getPublicationYear())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(book.getCategory())).append(CSV_SEPARATOR);
                writer.append(String.valueOf(book.getTotalStock())).append(CSV_SEPARATOR);
                writer.append(String.valueOf(book.getAvailableStock())).append(CSV_SEPARATOR);
                writer.append(book.isAvailable() ? "Available" : "Out of Stock");
                writer.append("\n");
            }
            
            logger.logSuccess("REPORT_SERVICE", 
                String.format("Book catalog exported successfully. %d books exported to %s", 
                            books.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("REPORT_SERVICE", "Failed to export book catalog: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportOverdueLoansToCSv(String filePath) throws IOException, DatabaseException {
        logger.logInfo("REPORT_SERVICE", "Starting overdue loans CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Loan ID,Member Name,Member Number,Book Title,ISBN,Loan Date,Expected Return Date,Days Overdue,Penalty Amount\n");
            
            // Get all loans and filter overdue ones
            List<Loan> allLoans = loanService.getAllLoans();
            List<Loan> overdueLoans = allLoans.stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
            
            // Write overdue loan data
            for (Loan loan : overdueLoans) {
                writer.append(String.valueOf(loan.getId())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getMember().getFullName())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getMember().getMemberNumber())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getBook().getTitle())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getBook().getIsbn())).append(CSV_SEPARATOR);
                writer.append(loan.getLoanDate().toString()).append(CSV_SEPARATOR);
                writer.append(loan.getExpectedReturnDate().toString()).append(CSV_SEPARATOR);
                writer.append(String.valueOf(loan.getOverdueDays())).append(CSV_SEPARATOR);
                writer.append(loan.getPenalty() != null ? loan.getPenalty().toString() : "0.00");
                writer.append("\n");
            }
            
            logger.logSuccess("REPORT_SERVICE", 
                String.format("Overdue loans exported successfully. %d overdue loans exported to %s", 
                            overdueLoans.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("REPORT_SERVICE", "Failed to export overdue loans: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportActiveLoansToCSv(String filePath) throws IOException, DatabaseException {
        logger.logInfo("REPORT_SERVICE", "Starting active loans CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Loan ID,Member Name,Member Number,Book Title,ISBN,Loan Date,Expected Return Date,Status,Days Until Due,Penalty Amount\n");
            
            // Get all loans and filter active ones
            List<Loan> allLoans = loanService.getAllLoans();
            List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE || loan.getStatus() == Loan.LoanStatus.OVERDUE)
                .collect(Collectors.toList());
            
            // Write active loan data
            for (Loan loan : activeLoans) {
                writer.append(String.valueOf(loan.getId())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getMember().getFullName())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getMember().getMemberNumber())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getBook().getTitle())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(loan.getBook().getIsbn())).append(CSV_SEPARATOR);
                writer.append(loan.getLoanDate().toString()).append(CSV_SEPARATOR);
                writer.append(loan.getExpectedReturnDate().toString()).append(CSV_SEPARATOR);
                writer.append(loan.getStatus().toString()).append(CSV_SEPARATOR);
                
                // Calculate days until due (negative if overdue)
                long daysUntilDue = loan.isOverdue() ? -loan.getOverdueDays() : 
                    java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), loan.getExpectedReturnDate());
                writer.append(String.valueOf(daysUntilDue)).append(CSV_SEPARATOR);
                
                writer.append(loan.getPenalty() != null ? loan.getPenalty().toString() : "0.00");
                writer.append("\n");
            }
            
            logger.logSuccess("REPORT_SERVICE", 
                String.format("Active loans exported successfully. %d active loans exported to %s", 
                            activeLoans.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("REPORT_SERVICE", "Failed to export active loans: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportMembersToCSv(String filePath) throws IOException, DatabaseException {
        logger.logInfo("REPORT_SERVICE", "Starting members CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("ID,Member Number,First Name,Last Name,Document ID,Email,Phone,Address,Registration Date,Active,Membership Type,Max Loans\n");
            
            // Get all members
            List<Member> members = memberService.getAllMembers();
            
            // Write member data
            for (Member member : members) {
                writer.append(String.valueOf(member.getId())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getMemberNumber())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getFirstName())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getLastName())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getDocumentId())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getEmail())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getPhone())).append(CSV_SEPARATOR);
                writer.append(escapeCsvField(member.getAddress())).append(CSV_SEPARATOR);
                writer.append(member.getRegistrationDate() != null ? member.getRegistrationDate().toString() : "").append(CSV_SEPARATOR);
                writer.append(member.getActive() ? "Active" : "Inactive").append(CSV_SEPARATOR);
                writer.append(member.getMembershipType() != null ? member.getMembershipType().toString() : "").append(CSV_SEPARATOR);
                writer.append(member.getMembershipType() != null ? String.valueOf(member.getMembershipType().getMaxLoans()) : "0");
                writer.append("\n");
            }
            
            logger.logSuccess("REPORT_SERVICE", 
                String.format("Members exported successfully. %d members exported to %s", 
                            members.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("REPORT_SERVICE", "Failed to export members: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Escapes CSV field content to handle commas, quotes, and newlines.
     * 
     * @param field the field content to escape
     * @return the escaped field content
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains comma, quote, or newline, wrap in quotes and escape existing quotes
        if (field.contains(CSV_SEPARATOR) || field.contains(CSV_QUOTE) || field.contains("\n") || field.contains("\r")) {
            return CSV_QUOTE + field.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE) + CSV_QUOTE;
        }
        
        return field;
    }
    
    /**
     * Generates a timestamped filename for reports.
     * 
     * @param baseFileName the base filename without extension
     * @return the timestamped filename
     */
    public String generateTimestampedFileName(String baseFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.csv", baseFileName, timestamp);
    }
}