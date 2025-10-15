package com.mycompany.booknova.ui;

import com.mycompany.booknova.domain.User;
import com.mycompany.booknova.service.auth.MockAuthenticationService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Login window controller for LibroNova application.
 * Handles user authentication and launches main application.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoginController extends Application {
    
    private Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;
    private final MockAuthenticationService authService = MockAuthenticationService.getInstance();
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginWindow();
    }
    
    /**
     * Creates and displays the login window.
     */
    private void showLoginWindow() {
        primaryStage.setTitle("LibroNova - Login");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        VBox mainContainer = createMainContainer();
        Scene scene = new Scene(mainContainer, 400, 500);
        
        // Add some basic styling
        scene.getStylesheets().add("data:text/css," +
            ".login-container { " +
            "    -fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e); " +
            "} " +
            ".login-panel { " +
            "    -fx-background-color: white; " +
            "    -fx-background-radius: 10; " +
            "    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 5); " +
            "} " +
            ".title-label { " +
            "    -fx-font-size: 28px; " +
            "    -fx-font-weight: bold; " +
            "    -fx-text-fill: #2c3e50; " +
            "} " +
            ".subtitle-label { " +
            "    -fx-font-size: 14px; " +
            "    -fx-text-fill: #7f8c8d; " +
            "} " +
            ".field-label { " +
            "    -fx-font-size: 12px; " +
            "    -fx-font-weight: bold; " +
            "    -fx-text-fill: #2c3e50; " +
            "} " +
            ".login-button { " +
            "    -fx-background-color: #3498db; " +
            "    -fx-text-fill: white; " +
            "    -fx-font-size: 14px; " +
            "    -fx-font-weight: bold; " +
            "    -fx-padding: 10px 20px; " +
            "    -fx-background-radius: 5; " +
            "    -fx-cursor: hand; " +
            "} " +
            ".login-button:hover { " +
            "    -fx-background-color: #2980b9; " +
            "} " +
            ".exit-button { " +
            "    -fx-background-color: #e74c3c; " +
            "    -fx-text-fill: white; " +
            "    -fx-font-size: 12px; " +
            "    -fx-padding: 5px 15px; " +
            "    -fx-background-radius: 3; " +
            "    -fx-cursor: hand; " +
            "} " +
            ".exit-button:hover { " +
            "    -fx-background-color: #c0392b; " +
            "} " +
            ".info-text { " +
            "    -fx-font-size: 11px; " +
            "    -fx-text-fill: #95a5a6; " +
            "} " +
            ".error-text { " +
            "    -fx-text-fill: #e74c3c; " +
            "    -fx-font-size: 12px; " +
            "} " +
            ".success-text { " +
            "    -fx-text-fill: #27ae60; " +
            "    -fx-font-size: 12px; " +
            "}"
        );
        
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Focus on username field
        usernameField.requestFocus();
    }
    
    /**
     * Creates the main container with login form.
     */
    private VBox createMainContainer() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("login-container");
        
        // Exit button (top right)
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Button exitButton = new Button("✕");
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> System.exit(0));
        topBar.getChildren().add(exitButton);
        
        // Login panel
        VBox loginPanel = createLoginPanel();
        loginPanel.getStyleClass().add("login-panel");
        
        VBox.setVgrow(loginPanel, Priority.NEVER);
        container.getChildren().addAll(topBar, loginPanel);
        
        return container;
    }
    
    /**
     * Creates the login panel with form fields.
     */
    private VBox createLoginPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40, 30, 40, 30));
        panel.setMaxWidth(340);
        
        // Logo and title
        Label titleLabel = new Label("📚 LibroNova");
        titleLabel.getStyleClass().add("title-label");
        
        Label subtitleLabel = new Label("Library Management System");
        subtitleLabel.getStyleClass().add("subtitle-label");
        
        // Username field
        Label userLabel = new Label("Username:");
        userLabel.getStyleClass().add("field-label");
        
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(35);
        usernameField.setOnAction(e -> passwordField.requestFocus());
        
        // Password field
        Label passLabel = new Label("Password:");
        passLabel.getStyleClass().add("field-label");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(35);
        passwordField.setOnAction(e -> handleLogin());
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefWidth(280);
        loginButton.setPrefHeight(40);
        loginButton.setOnAction(e -> handleLogin());
        
        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);
        
        // Information section
        VBox infoBox = createInfoSection();
        
        panel.getChildren().addAll(
            titleLabel, subtitleLabel,
            new Separator(),
            userLabel, usernameField,
            passLabel, passwordField,
            loginButton,
            messageLabel,
            new Separator(),
            infoBox
        );
        
        return panel;
    }
    
    /**
     * Creates the information section with available users.
     */
    private VBox createInfoSection() {
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        
        Label infoTitle = new Label("Demo Users:");
        infoTitle.getStyleClass().add("field-label");
        
        Label adminInfo = new Label("Administrator: admin / admin");
        adminInfo.getStyleClass().add("info-text");
        
        Label librarianInfo = new Label("Librarian: librarian / librarian");
        librarianInfo.getStyleClass().add("info-text");
        
        Label assistantInfo = new Label("Assistant: assistant / assistant");
        assistantInfo.getStyleClass().add("info-text");
        
        infoBox.getChildren().addAll(infoTitle, adminInfo, librarianInfo, assistantInfo);
        return infoBox;
    }
    
    /**
     * Handles the login process.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", "error");
            return;
        }
        
        User user = authService.login(username, password);
        
        if (user != null) {
            showMessage("Login successful! Welcome " + user.getFirstName() + "!", "success");
            
            // Simple delay without Timeline
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        primaryStage.close();
                        launchMainApplication();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            showMessage("Invalid username or password. Please try again.", "error");
            passwordField.clear();
            usernameField.requestFocus();
        }
    }
    
    /**
     * Shows a message to the user.
     */
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add(type + "-text");
    }
    
    /**
     * Launches the main application after successful login.
     */
    private void launchMainApplication() {
        try {
            Stage mainStage = new Stage();
            MainApp mainApp = new MainApp();
            mainApp.start(mainStage);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error launching main application: " + e.getMessage(), "error");
        }
    }
    
    /**
     * Main method to run the login application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
