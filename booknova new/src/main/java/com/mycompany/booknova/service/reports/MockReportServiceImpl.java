package com.mycompany.booknova.service.reports;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.infra.logging.AppLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of ReportService for testing and demonstration.
 * Uses pre-defined mock data instead of database connections.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MockReportServiceImpl implements ReportService {
    
    private final AppLogger logger;
    
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_QUOTE = "\"";
    
    public MockReportServiceImpl() {
        this.logger = AppLogger.getInstance();
    }
    
    @Override
    public boolean exportBookCatalogToCsv(String filePath) throws IOException {
        logger.logInfo("MOCK_REPORT_SERVICE", "Starting mock book catalog CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("ID,ISBN,Title,Author,Publisher,Publication Year,Category,Total Stock,Available Stock,Status\n");
            
            // Get mock books
            List<Book> books = getMockBooks();
            
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
            
            logger.logSuccess("MOCK_REPORT_SERVICE", 
                String.format("Mock book catalog exported successfully. %d books exported to %s", 
                            books.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("MOCK_REPORT_SERVICE", "Failed to export mock book catalog: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportOverdueLoansToCSv(String filePath) throws IOException {
        logger.logInfo("MOCK_REPORT_SERVICE", "Starting mock overdue loans CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Loan ID,Member Name,Member Number,Book Title,ISBN,Loan Date,Expected Return Date,Days Overdue,Penalty Amount\n");
            
            // Get mock loans and filter overdue ones
            List<Loan> overdueLoans = getMockLoans().stream()
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
            
            logger.logSuccess("MOCK_REPORT_SERVICE", 
                String.format("Mock overdue loans exported successfully. %d overdue loans exported to %s", 
                            overdueLoans.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("MOCK_REPORT_SERVICE", "Failed to export mock overdue loans: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportActiveLoansToCSv(String filePath) throws IOException {
        logger.logInfo("MOCK_REPORT_SERVICE", "Starting mock active loans CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Loan ID,Member Name,Member Number,Book Title,ISBN,Loan Date,Expected Return Date,Status,Days Until Due,Penalty Amount\n");
            
            // Get mock active loans
            List<Loan> activeLoans = getMockLoans().stream()
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
                
                // Calculate days until due
                long daysUntilDue = loan.isOverdue() ? -loan.getOverdueDays() : 
                    java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), loan.getExpectedReturnDate());
                writer.append(String.valueOf(daysUntilDue)).append(CSV_SEPARATOR);
                
                writer.append(loan.getPenalty() != null ? loan.getPenalty().toString() : "0.00");
                writer.append("\n");
            }
            
            logger.logSuccess("MOCK_REPORT_SERVICE", 
                String.format("Mock active loans exported successfully. %d active loans exported to %s", 
                            activeLoans.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("MOCK_REPORT_SERVICE", "Failed to export mock active loans: " + e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean exportMembersToCSv(String filePath) throws IOException {
        logger.logInfo("MOCK_REPORT_SERVICE", "Starting mock members CSV export to: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("ID,Member Number,First Name,Last Name,Document ID,Email,Phone,Address,Registration Date,Active,Membership Type,Max Loans\n");
            
            // Get mock members
            List<Member> members = getMockMembers();
            
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
            
            logger.logSuccess("MOCK_REPORT_SERVICE", 
                String.format("Mock members exported successfully. %d members exported to %s", 
                            members.size(), filePath));
            return true;
            
        } catch (Exception e) {
            logger.logError("MOCK_REPORT_SERVICE", "Failed to export mock members: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Creates mock book data for testing.
     */
    private List<Book> getMockBooks() {
        return Arrays.asList(
            createBook(1L, "978-0-134-68566-1", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008, "Technology", 5, 3),
            createBook(2L, "978-0-201-61622-4", "The Pragmatic Programmer", "Andrew Hunt", "Addison-Wesley", 1999, "Technology", 3, 1),
            createBook(3L, "978-0-596-52068-7", "JavaScript: The Good Parts", "Douglas Crockford", "O'Reilly", 2008, "Technology", 4, 2),
            createBook(4L, "978-0-321-12521-7", "Domain-Driven Design", "Eric Evans", "Addison-Wesley", 2003, "Technology", 2, 0),
            createBook(5L, "978-0-307-38789-9", "The Lean Startup", "Eric Ries", "Crown Business", 2011, "Business", 6, 4)
        );
    }
    
    /**
     * Creates mock member data for testing.
     */
    private List<Member> getMockMembers() {
        return Arrays.asList(
            createMember(1L, "M001", "Juan", "Pérez", "12345678", "juan.perez@email.com", "+1234567890", "Calle 123 #45-67", LocalDate.of(2023, 1, 15), true, Member.MembershipType.PREMIUM),
            createMember(2L, "M002", "María", "González", "87654321", "maria.gonzalez@email.com", "+1234567891", "Carrera 89 #12-34", LocalDate.of(2023, 3, 20), true, Member.MembershipType.BASIC),
            createMember(3L, "M003", "Carlos", "Rodríguez", "11223344", "carlos.rodriguez@email.com", "+1234567892", "Avenida 56 #78-90", LocalDate.of(2023, 5, 10), false, Member.MembershipType.VIP),
            createMember(4L, "M004", "Ana", "López", "44332211", "ana.lopez@email.com", "+1234567893", "Diagonal 23 #45-67", LocalDate.of(2023, 7, 5), true, Member.MembershipType.BASIC)
        );
    }
    
    /**
     * Creates mock loan data for testing.
     */
    private List<Loan> getMockLoans() {
        List<Book> books = getMockBooks();
        List<Member> members = getMockMembers();
        User user = createUser(1L, "admin", "Administrator");
        
        return Arrays.asList(
            createLoan(1L, members.get(0), books.get(0), user, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3), null, Loan.LoanStatus.OVERDUE, new BigDecimal("10.50")),
            createLoan(2L, members.get(1), books.get(1), user, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), null, Loan.LoanStatus.ACTIVE, BigDecimal.ZERO),
            createLoan(3L, members.get(2), books.get(2), user, LocalDate.now().minusDays(20), LocalDate.now().minusDays(5), LocalDate.now().minusDays(2), Loan.LoanStatus.RETURNED, BigDecimal.ZERO),
            createLoan(4L, members.get(3), books.get(3), user, LocalDate.now().minusDays(8), LocalDate.now().plusDays(7), null, Loan.LoanStatus.ACTIVE, BigDecimal.ZERO)
        );
    }
    
    private Book createBook(Long id, String isbn, String title, String author, String publisher, Integer year, String category, Integer totalStock, Integer availableStock) {
        Book book = new Book();
        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationYear(year);
        book.setCategory(category);
        book.setTotalStock(totalStock);
        book.setAvailableStock(availableStock);
        return book;
    }
    
    private Member createMember(Long id, String memberNumber, String firstName, String lastName, String documentId, String email, String phone, String address, LocalDate registrationDate, Boolean active, Member.MembershipType membershipType) {
        Member member = new Member();
        member.setId(id);
        member.setMemberNumber(memberNumber);
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setDocumentId(documentId);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAddress(address);
        member.setRegistrationDate(registrationDate);
        member.setActive(active);
        member.setMembershipType(membershipType);
        return member;
    }
    
    private Loan createLoan(Long id, Member member, Book book, User user, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate, Loan.LoanStatus status, BigDecimal penalty) {
        Loan loan = new Loan();
        loan.setId(id);
        loan.setMember(member);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(loanDate);
        loan.setExpectedReturnDate(expectedReturnDate);
        loan.setActualReturnDate(actualReturnDate);
        loan.setStatus(status);
        loan.setPenalty(penalty);
        return loan;
    }
    
    private User createUser(Long id, String username, String fullName) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName(fullName);
        user.setLastName("");
        user.setRole(User.UserRole.ADMINISTRATOR);
        user.setActive(true);
        return user;
    }
    
    /**
     * Escapes CSV field content to handle commas, quotes, and newlines.
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        if (field.contains(CSV_SEPARATOR) || field.contains(CSV_QUOTE) || field.contains("\n") || field.contains("\r")) {
            return CSV_QUOTE + field.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE) + CSV_QUOTE;
        }
        
        return field;
    }
    
    /**
     * Generates a timestamped filename for reports.
     */
    public String generateTimestampedFileName(String baseFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.csv", baseFileName, timestamp);
    }
}