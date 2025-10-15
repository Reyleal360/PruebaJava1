package com.mycompany.booknova.ui;

import com.mycompany.booknova.infra.logging.AppLogger;
import com.mycompany.booknova.service.reports.ReportService;
import com.mycompany.booknova.service.reports.ReportServiceImpl;
import com.mycompany.booknova.service.reports.MockReportServiceImpl;
import com.mycompany.booknova.service.auth.MockAuthenticationService;
import com.mycompany.booknova.domain.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application view with navigation to all modules.
 * Serves as the entry point and dashboard for LibroNova system.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MainApp extends Application {
    
    private BorderPane mainLayout;
    private Label statusLabel;
    private StackPane contentArea;
    private Stage primaryStage;
    private final AppLogger logger = AppLogger.getInstance();
    private final ReportService reportService = new MockReportServiceImpl();
    private final MockAuthenticationService authService = MockAuthenticationService.getInstance();
    private User currentUser;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Get current user from authentication service
        currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, create a default admin user for direct access
            logger.logError("APPLICATION", "No authenticated user found, creating default session");
            currentUser = createDefaultUser();
        }
        
        logger.logApplicationStart();
        logger.logUserActivity(currentUser.getFullName(), "Main application started");
        
        primaryStage.setTitle("LibroNova - Library Management System");
        
        mainLayout = new BorderPane();
        mainLayout.setTop(createTopBar());
        mainLayout.setLeft(createNavigationMenu());
        mainLayout.setCenter(createContentArea());
        mainLayout.setBottom(createStatusBar());
        
        Scene scene = new Scene(mainLayout, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Show dashboard by default
        showDashboard();
    }
    
    /**
     * Creates the top bar with application title and info.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("📚 LibroNova - Library Management System");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
        
        // Add logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 3px 8px; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> handleLogout(primaryStage));
        
        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutBtn);
        return topBar;
    }
    
    /**
     * Creates the left navigation menu.
     */
    private VBox createNavigationMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #34495e;");
        
        Button dashboardBtn = createMenuButton("🏠 Dashboard");
        Button booksBtn = createMenuButton("📖 Books");
        Button membersBtn = createMenuButton("👥 Members");
        Button loansBtn = createMenuButton("📋 Loans");
        Button reportsBtn = createMenuButton("📊 Reports");
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        Button settingsBtn = createMenuButton("⚙️ Settings");
        Button exitBtn = createMenuButton("🚪 Exit");
        
        // Set actions
        dashboardBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Dashboard");
            showDashboard();
        });
        booksBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Books module");
            showBooksView();
        });
        membersBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Members module");
            showMembersView();
        });
        loansBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Loans module");
            showLoansView();
        });
        reportsBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Reports module");
            showReportsView();
        });
        settingsBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Navigated to Settings");
            showSettings();
        });
        exitBtn.setOnAction(e -> {
            logger.logUserActivity(currentUser.getFullName(), "Application exit requested");
            logger.logApplicationShutdown();
            System.exit(0);
        });
        
        menu.getChildren().addAll(
            dashboardBtn, booksBtn, membersBtn, loansBtn, 
            reportsBtn, separator, settingsBtn, exitBtn
        );
        
        return menu;
    }
    
    /**
     * Creates a styled menu button.
     */
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #ecf0f1; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12px 15px; " +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12px 15px; " +
                "-fx-cursor: hand;"
            )
        );
        
        btn.setOnMouseExited(e -> 
            btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #ecf0f1; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12px 15px; " +
                "-fx-cursor: hand;"
            )
        );
        
        return btn;
    }
    
    /**
     * Creates the main content area.
     */
    private StackPane createContentArea() {
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #ecf0f1;");
        return contentArea;
    }
    
    /**
     * Creates the bottom status bar.
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(8, 15, 8, 15));
        statusBar.setStyle("-fx-background-color: #95a5a6;");
        
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("v1.0.0");
        versionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }
    
    /**
     * Shows the dashboard view.
     */
    private void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        dashboard.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Welcome to LibroNova");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        
        // Statistics cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        VBox booksCard = createStatCard("📚", "Total Books", "0", "#3498db");
        VBox membersCard = createStatCard("👥", "Active Members", "0", "#2ecc71");
        VBox loansCard = createStatCard("📋", "Active Loans", "0", "#e74c3c");
        VBox overdueCard = createStatCard("⚠️", "Overdue Loans", "0", "#f39c12");
        
        statsCards.getChildren().addAll(booksCard, membersCard, loansCard, overdueCard);
        
        dashboard.getChildren().addAll(title, statsCards);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboard);
        updateStatus("Dashboard loaded");
    }
    
    /**
     * Creates a statistics card for the dashboard.
     */
    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setPrefSize(220, 140);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);"
        );
        card.setPadding(new Insets(20));
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        return card;
    }
    
    /**
     * Shows the books management view.
     */
        private void showBooksView() {
            try {
                BooksView booksView = new BooksView();
                VBox view = booksView.getView();
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
                updateStatus("Books module loaded");
            } catch (Exception e) {
                showError("Error loading Books view: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Shows the members management view.
         */
        private void showMembersView() {
            try {
                MembersView membersView = new MembersView();
                VBox view = membersView.getView();
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
                updateStatus("Members module loaded");
            } catch (Exception e) {
                showError("Error loading Members view: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
    /**
     * Shows the loans management view.
     */
    private void showLoansView() {
        try {
            LoansView loansView = new LoansView();
            VBox view = loansView.getView();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            updateStatus("Loans module loaded");
        } catch (Exception e) {
            showError("Error loading Loans view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the reports view.
     */
    private void showReportsView() {
        VBox reportsView = new VBox(20);
        reportsView.setPadding(new Insets(20));
        
        Label title = new Label("📊 Reports & Statistics");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label description = new Label("Export system data to CSV format for analysis and reporting.");
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Create export buttons section
        VBox exportSection = new VBox(15);
        exportSection.setPadding(new Insets(20));
        exportSection.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label exportTitle = new Label("📁 Data Export");
        exportTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Export buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button exportBooksBtn = createExportButton("📚 Book Catalog", "Export complete book catalog");
        Button exportOverdueBtn = createExportButton("⚠️ Overdue Loans", "Export overdue loans");
        Button exportActiveBtn = createExportButton("📋 Active Loans", "Export active loans");
        Button exportMembersBtn = createExportButton("👥 Members List", "Export members list");
        
        // Set button actions
        exportBooksBtn.setOnAction(e -> exportReport("books"));
        exportOverdueBtn.setOnAction(e -> exportReport("overdue"));
        exportActiveBtn.setOnAction(e -> exportReport("active"));
        exportMembersBtn.setOnAction(e -> exportReport("members"));
        
        buttonBox.getChildren().addAll(exportBooksBtn, exportOverdueBtn, exportActiveBtn, exportMembersBtn);
        
        exportSection.getChildren().addAll(exportTitle, buttonBox);
        
        reportsView.getChildren().addAll(title, description, exportSection);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(reportsView);
        updateStatus("Reports module loaded");
    }
    
    /**
     * Shows the settings view.
     */
    private void showSettings() {
        VBox settingsView = new VBox(20);
        settingsView.setPadding(new Insets(20));
        
        Label title = new Label("⚙️ System Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label info = new Label("Settings module coming soon...");
        info.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        settingsView.getChildren().addAll(title, info);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(settingsView);
        updateStatus("Settings loaded");
    }
    
    /**
     * Updates the status bar message.
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Shows an error alert.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus("Error occurred");
    }
    
    /**
     * Creates a styled export button.
     */
    private Button createExportButton(String title, String description) {
        VBox buttonContent = new VBox(5);
        buttonContent.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        buttonContent.getChildren().addAll(titleLabel, descLabel);
        
        Button btn = new Button();
        btn.setGraphic(buttonContent);
        btn.setPrefSize(140, 80);
        btn.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
        );
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle(
                "-fx-background-color: #2980b9; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            )
        );
        
        btn.setOnMouseExited(e -> 
            btn.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
            )
        );
        
        return btn;
    }
    
    /**
     * Handles report export based on type.
     */
    private void exportReport(String reportType) {
        try {
            String fileName = "";
            boolean success = false;
            
            switch (reportType) {
                case "books":
                    fileName = "book_catalog_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
                    success = reportService.exportBookCatalogToCsv(fileName);
                    logger.logUserActivity(currentUser.getFullName(), "Exported book catalog to CSV: " + fileName);
                    break;
                    
                case "overdue":
                    fileName = "overdue_loans_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
                    success = reportService.exportOverdueLoansToCSv(fileName);
                    logger.logUserActivity(currentUser.getFullName(), "Exported overdue loans to CSV: " + fileName);
                    break;
                    
                case "active":
                    fileName = "active_loans_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
                    success = reportService.exportActiveLoansToCSv(fileName);
                    logger.logUserActivity(currentUser.getFullName(), "Exported active loans to CSV: " + fileName);
                    break;
                    
                case "members":
                    fileName = "members_list_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
                    success = reportService.exportMembersToCSv(fileName);
                    logger.logUserActivity(currentUser.getFullName(), "Exported members list to CSV: " + fileName);
                    break;
            }
            
            if (success) {
                showInfo("Export completed successfully!", "File saved as: " + fileName);
                updateStatus("Export completed: " + fileName);
            } else {
                showError("Export failed. Please check the logs for details.");
            }
            
        } catch (Exception e) {
            logger.logError("MAIN_APP", "Error during report export: " + e.getMessage(), e);
            showError("Export failed: " + e.getMessage());
        }
    }
    
    /**
     * Shows an info alert.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Creates a default user when no authenticated user is found.
     */
    private User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(999L);
        defaultUser.setUsername("system");
        defaultUser.setFirstName("System");
        defaultUser.setLastName("Administrator");
        defaultUser.setEmail("system@libronova.com");
        defaultUser.setRole(User.UserRole.ADMINISTRATOR);
        defaultUser.setActive(true);
        return defaultUser;
    }
    
    /**
     * Handles user logout.
     */
    private void handleLogout(Stage primaryStage) {
        try {
            authService.logout();
            logger.logUserActivity(currentUser.getFullName(), "User logged out, returning to login screen");
            
            // Close current window
            primaryStage.close();
            
            // Show login window
            Stage loginStage = new Stage();
            LoginController loginController = new LoginController();
            loginController.start(loginStage);
            
        } catch (Exception e) {
            logger.logError("MAIN_APP", "Error during logout: " + e.getMessage(), e);
            showError("Error during logout: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
