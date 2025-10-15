import com.mycompany.booknova.service.auth.MockAuthenticationService;
import com.mycompany.booknova.domain.User;

public class TestLogin {
    public static void main(String[] args) {
        MockAuthenticationService authService = MockAuthenticationService.getInstance();
        
        System.out.println("=== Testing LibroNova Authentication System ===");
        
        // Test admin login
        System.out.println("\n1. Testing Admin Login:");
        User admin = authService.login("admin", "admin");
        if (admin != null) {
            System.out.println("✅ Admin login SUCCESS");
            System.out.println("   User: " + admin.getFullName());
            System.out.println("   Role: " + admin.getRole());
            System.out.println("   Email: " + admin.getEmail());
        } else {
            System.out.println("❌ Admin login FAILED");
        }
        
        // Logout
        authService.logout();
        
        // Test librarian login
        System.out.println("\n2. Testing Librarian Login:");
        User librarian = authService.login("librarian", "librarian");
        if (librarian != null) {
            System.out.println("✅ Librarian login SUCCESS");
            System.out.println("   User: " + librarian.getFullName());
            System.out.println("   Role: " + librarian.getRole());
            System.out.println("   Email: " + librarian.getEmail());
        } else {
            System.out.println("❌ Librarian login FAILED");
        }
        
        // Logout
        authService.logout();
        
        // Test assistant login
        System.out.println("\n3. Testing Assistant Login:");
        User assistant = authService.login("assistant", "assistant");
        if (assistant != null) {
            System.out.println("✅ Assistant login SUCCESS");
            System.out.println("   User: " + assistant.getFullName());
            System.out.println("   Role: " + assistant.getRole());
            System.out.println("   Email: " + assistant.getEmail());
        } else {
            System.out.println("❌ Assistant login FAILED");
        }
        
        // Logout
        authService.logout();
        
        // Test invalid login
        System.out.println("\n4. Testing Invalid Login:");
        User invalid = authService.login("invalid", "wrong");
        if (invalid == null) {
            System.out.println("✅ Invalid login properly REJECTED");
        } else {
            System.out.println("❌ Invalid login incorrectly ACCEPTED");
        }
        
        // Test empty credentials
        System.out.println("\n5. Testing Empty Credentials:");
        User empty = authService.login("", "");
        if (empty == null) {
            System.out.println("✅ Empty credentials properly REJECTED");
        } else {
            System.out.println("❌ Empty credentials incorrectly ACCEPTED");
        }
        
        // Test all users
        System.out.println("\n6. Available Users:");
        authService.getAllUsers().forEach(user -> {
            System.out.println("   • " + user.getUsername() + " / " + user.getUsername() + 
                             " (" + user.getFullName() + " - " + user.getRole() + ")");
        });
        
        System.out.println("\n=== Authentication System Test Complete ===");
    }
}