import com.mycompany.booknova.service.reports.MockReportServiceImpl;

public class TestMockReports {
    public static void main(String[] args) {
        try {
            MockReportServiceImpl mockService = new MockReportServiceImpl();
            
            System.out.println("=== Testing MockReportService ===");
            
            // Test book catalog export
            System.out.println("Testing book catalog export...");
            boolean result1 = mockService.exportBookCatalogToCsv("test_books.csv");
            System.out.println("Book catalog export: " + (result1 ? "SUCCESS" : "FAILED"));
            
            // Test overdue loans export
            System.out.println("Testing overdue loans export...");
            boolean result2 = mockService.exportOverdueLoansToCSv("test_overdue.csv");
            System.out.println("Overdue loans export: " + (result2 ? "SUCCESS" : "FAILED"));
            
            // Test active loans export
            System.out.println("Testing active loans export...");
            boolean result3 = mockService.exportActiveLoansToCSv("test_active.csv");
            System.out.println("Active loans export: " + (result3 ? "SUCCESS" : "FAILED"));
            
            // Test members export
            System.out.println("Testing members export...");
            boolean result4 = mockService.exportMembersToCSv("test_members.csv");
            System.out.println("Members export: " + (result4 ? "SUCCESS" : "FAILED"));
            
            System.out.println("=== All tests completed ===");
            System.out.println("Overall result: " + 
                (result1 && result2 && result3 && result4 ? "ALL SUCCESS" : "SOME FAILURES"));
                
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}