package com.mycompany.booknova.ui;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.domain.Loan;
import com.mycompany.booknova.domain.Loan.LoanStatus;
import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.domain.Member.MembershipType;
import com.mycompany.booknova.service.BookService;
import com.mycompany.booknova.service.LoanService;
import com.mycompany.booknova.service.MemberService;
import com.mycompany.booknova.service.impl.BookServiceImpl;
import com.mycompany.booknova.service.impl.LoanServiceImpl;
import com.mycompany.booknova.service.impl.MemberServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Loans management view.
 * Handles loan creation, returns, and tracking.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class LoansView {
    
    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;
    private final VBox mainLayout;
    private TableView<LoanTableModel> loansTable;
    private ObservableList<LoanTableModel> loansList;
    private ComboBox<String> statusFilter;
    
    // Hardcoded user ID for now (since no login)
    private static final Long CURRENT_USER_ID = 1L;
    
    public LoansView() {
        this.loanService = new LoanServiceImpl();
        this.bookService = new BookServiceImpl();
        this.memberService = new MemberServiceImpl();
        this.mainLayout = new VBox(15);
        this.loansList = FXCollections.observableArrayList();
        
        initializeView();
        loadLoans();
    }
    
    /**
     * Initializes the view components.
     */
    private void initializeView() {
        mainLayout.setPadding(new Insets(20));
        
        HBox header = createHeader();
        HBox filterBar = createFilterBar();
        loansTable = createLoansTable();
        VBox.setVgrow(loansTable, Priority.ALWAYS);
        HBox actionButtons = createActionButtons();
        
        mainLayout.getChildren().addAll(header, filterBar, loansTable, actionButtons);
    }
    
    /**
     * Creates the header section.
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📋 Loans Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button newLoanButton = new Button("+ New Loan");
        newLoanButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        );
        newLoanButton.setOnAction(e -> showNewLoanDialog());
        
        Button overdueButton = new Button("⚠️ View Overdue");
        overdueButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        );
        overdueButton.setOnAction(e -> showOverdueLoans());
        
        header.getChildren().addAll(title, spacer, overdueButton, newLoanButton);
        return header;
    }
    
    /**
     * Creates the filter bar.
     */
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold;");
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Loans", "ACTIVE", "RETURNED", "OVERDUE", "RENEWED");
        statusFilter.setValue("All Loans");
        statusFilter.setOnAction(e -> filterLoans());
        
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> {
            statusFilter.setValue("All Loans");
            loadLoans();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label infoLabel = new Label("💡 Select a loan and click 'Return Book' to process returns");
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        filterBar.getChildren().addAll(
            statusLabel, statusFilter, clearButton, spacer, infoLabel
        );
        
        return filterBar;
    }
    
    /**
     * Creates the loans table.
     */
    private TableView<LoanTableModel> createLoansTable() {
        TableView<LoanTableModel> table = new TableView<>();
        table.setItems(loansList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<LoanTableModel, Long> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        idCol.setPrefWidth(70);
        
        TableColumn<LoanTableModel, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        memberCol.setPrefWidth(150);
        
        TableColumn<LoanTableModel, String> memberNumCol = new TableColumn<>("Member #");
        memberNumCol.setCellValueFactory(new PropertyValueFactory<>("memberNumber"));
        memberNumCol.setPrefWidth(120);
        
        TableColumn<LoanTableModel, String> bookCol = new TableColumn<>("Book Title");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookCol.setPrefWidth(200);
        
        TableColumn<LoanTableModel, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(100);
        
        TableColumn<LoanTableModel, String> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        loanDateCol.setPrefWidth(100);
        
        TableColumn<LoanTableModel, String> expectedReturnCol = new TableColumn<>("Expected Return");
        expectedReturnCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        expectedReturnCol.setPrefWidth(120);
        
        TableColumn<LoanTableModel, String> actualReturnCol = new TableColumn<>("Actual Return");
        actualReturnCol.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        actualReturnCol.setPrefWidth(120);
        
        TableColumn<LoanTableModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(column -> new TableCell<LoanTableModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ACTIVE":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "RETURNED":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "OVERDUE":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "RENEWED":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
        
        TableColumn<LoanTableModel, String> penaltyCol = new TableColumn<>("Penalty");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penalty"));
        penaltyCol.setPrefWidth(80);
        
        table.getColumns().addAll(
            idCol, memberCol, memberNumCol, bookCol, isbnCol,
            loanDateCol, expectedReturnCol, actualReturnCol, statusCol, penaltyCol
        );
        
        return table;
    }
    
    /**
     * Creates the action buttons section.
     */
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button returnButton = new Button("Return Book");
        returnButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        returnButton.setOnAction(e -> returnSelectedLoan());
        
        Button renewButton = new Button("Renew Loan");
        renewButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        renewButton.setOnAction(e -> renewSelectedLoan());
        
        Button detailsButton = new Button("View Details");
        detailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> viewLoanDetails());
        
        Button refreshButton = new Button("↻ Refresh");
        refreshButton.setOnAction(e -> loadLoans());
        
        buttonBox.getChildren().addAll(returnButton, renewButton, detailsButton, refreshButton);
        return buttonBox;
    }
    
    /**
     * Loads all loans from the service.
     */
    private void loadLoans() {
        try {
            loansList.clear();
            var loans = loanService.getAllLoans();
            for (Loan loan : loans) {
                loansList.add(new LoanTableModel(loan));
            }
        } catch (Exception e) {
            showError("Error loading loans: " + e.getMessage());
        }
    }
    
    /**
     * Filters loans based on status.
     */
    private void filterLoans() {
        try {
            String selectedStatus = statusFilter.getValue();
            
            loansList.clear();
            var loans = loanService.getAllLoans();
            
            for (Loan loan : loans) {
                boolean matches = selectedStatus.equals("All Loans") ||
                    loan.getStatus().name().equals(selectedStatus);
                
                if (matches) {
                    loansList.add(new LoanTableModel(loan));
                }
            }
        } catch (Exception e) {
            showError("Error filtering loans: " + e.getMessage());
        }
    }
    
    /**
     * Shows overdue loans only.
     */
    private void showOverdueLoans() {
        try {
            loansList.clear();
            var loans = loanService.getOverdueLoans();
            for (Loan loan : loans) {
                loansList.add(new LoanTableModel(loan));
            }
            
            if (loans.isEmpty()) {
                showInfo("No overdue loans found!");
            }
        } catch (Exception e) {
            showError("Error loading overdue loans: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog to create a new loan.
     */
    private void showNewLoanDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create New Loan");
        dialog.setHeaderText("Borrow a Book");
        
        ButtonType createButtonType = new ButtonType("Create Loan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Member selection
        ComboBox<MemberComboItem> memberCombo = new ComboBox<>();
        try {
            var members = memberService.getActiveMembers();
            for (Member member : members) {
                memberCombo.getItems().add(new MemberComboItem(member));
            }
        } catch (Exception e) {
            showError("Error loading members: " + e.getMessage());
        }
        
        // Book selection
        ComboBox<BookComboItem> bookCombo = new ComboBox<>();
        try {
            var books = bookService.getAvailableBooks();
            for (Book book : books) {
                bookCombo.getItems().add(new BookComboItem(book));
            }
        } catch (Exception e) {
            showError("Error loading books: " + e.getMessage());
        }
        
        Label infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        infoLabel.setWrapText(true);
        
        // Show member info when selected
        memberCombo.setOnAction(e -> {
            MemberComboItem selected = memberCombo.getValue();
            if (selected != null) {
                try {
                    var activeLoans = loanService.getActiveLoansByMember(selected.getId());
                    int maxLoans = selected.getMembershipType().getMaxLoans();
                    infoLabel.setText(String.format(
                        "Active loans: %d/%d | Membership: %s",
                        activeLoans.size(), maxLoans, selected.getMembershipType()
                    ));
                } catch (Exception ex) {
                    infoLabel.setText("Error loading member info");
                }
            }
        });
        
        grid.add(new Label("Select Member:"), 0, 0);
        grid.add(memberCombo, 1, 0);
        grid.add(new Label("Select Book:"), 0, 1);
        grid.add(bookCombo, 1, 1);
        grid.add(infoLabel, 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                MemberComboItem selectedMember = memberCombo.getValue();
                BookComboItem selectedBook = bookCombo.getValue();
                
                if (selectedMember == null || selectedBook == null) {
                    showWarning("Please select both member and book");
                    return null;
                }
                
                try {
                    loanService.createLoan(
                        selectedMember.getId(),
                        selectedBook.getId(),
                        CURRENT_USER_ID
                    );
                    showSuccess("Loan created successfully");
                    loadLoans();
                } catch (Exception e) {
                    showError("Error creating loan: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Returns the selected loan.
     */
    private void returnSelectedLoan() {
        LoanTableModel selected = loansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a loan to return");
            return;
        }
        
        if (!selected.getStatus().equals("ACTIVE")) {
            showWarning("Only active loans can be returned");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Return Book");
        confirmAlert.setContentText(
            "Process return for:\n" +
            "Book: " + selected.getBookTitle() + "\n" +
            "Member: " + selected.getMemberName()
        );
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Loan returnedLoan = loanService.returnBook(selected.getLoanId());
                    
                    if (returnedLoan.getPenalty().compareTo(BigDecimal.ZERO) > 0) {
                        showInfo(String.format(
                            "Book returned successfully!\n\n" +
                            "Penalty amount: $%.2f\n" +
                            "Status: %s",
                            returnedLoan.getPenalty(),
                            returnedLoan.getStatus()
                        ));
                    } else {
                        showSuccess("Book returned successfully! No penalty.");
                    }
                    
                    loadLoans();
                } catch (Exception e) {
                    showError("Error returning book: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Renews the selected loan.
     */
    private void renewSelectedLoan() {
        LoanTableModel selected = loansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a loan to renew");
            return;
        }
        
        if (!selected.getStatus().equals("ACTIVE")) {
            showWarning("Only active loans can be renewed");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("7");
        dialog.setTitle("Renew Loan");
        dialog.setHeaderText("Extend loan period");
        dialog.setContentText("Additional days:");
        
        dialog.showAndWait().ifPresent(days -> {
            try {
                int additionalDays = Integer.parseInt(days);
                if (additionalDays <= 0 || additionalDays > 30) {
                    showWarning("Please enter a value between 1 and 30 days");
                    return;
                }
                
                loanService.renewLoan(selected.getLoanId(), additionalDays);
                showSuccess("Loan renewed successfully for " + additionalDays + " days");
                loadLoans();
            } catch (NumberFormatException e) {
                showError("Invalid number format");
            } catch (Exception e) {
                showError("Error renewing loan: " + e.getMessage());
            }
        });
    }
    
    /**
     * Views details of the selected loan.
     */
    private void viewLoanDetails() {
        LoanTableModel selected = loansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a loan to view");
            return;
        }
        
        try {
            Loan loan = loanService.findLoanById(selected.getLoanId());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Loan Details");
            alert.setHeaderText("Loan #" + loan.getId());
            
            String details = String.format(
                "Member: %s (%s)\n" +
                "Book: %s\n" +
                "ISBN: %s\n" +
                "Author: %s\n\n" +
                "Loan Date: %s\n" +
                "Expected Return: %s\n" +
                "Actual Return: %s\n" +
                "Status: %s\n" +
                "Penalty: $%.2f\n" +
                "Notes: %s\n\n" +
                "Processed by: %s",
                loan.getMember().getFullName(),
                loan.getMember().getMemberNumber(),
                loan.getBook().getTitle(),
                loan.getBook().getIsbn(),
                loan.getBook().getAuthor(),
                loan.getLoanDate(),
                loan.getExpectedReturnDate(),
                loan.getActualReturnDate() != null ? loan.getActualReturnDate() : "Not returned",
                loan.getStatus(),
                loan.getPenalty(),
                loan.getNotes() != null ? loan.getNotes() : "N/A",
                loan.getUser().getFullName()
            );
            
            alert.setContentText(details);
            alert.showAndWait();
        } catch (Exception e) {
            showError("Error loading loan details: " + e.getMessage());
        }
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public VBox getView() {
        return mainLayout;
    }
    
    /**
     * Model class for displaying loans in the table.
     */
    public static class LoanTableModel {
        private final Long loanId;
        private final String memberName;
        private final String memberNumber;
        private final String bookTitle;
        private final String isbn;
        private final String loanDate;
        private final String expectedReturnDate;
        private final String actualReturnDate;
        private final String status;
        private final String penalty;
        
        public LoanTableModel(Loan loan) {
            this.loanId = loan.getId();
            this.memberName = loan.getMember().getFullName();
            this.memberNumber = loan.getMember().getMemberNumber();
            this.bookTitle = loan.getBook().getTitle();
            this.isbn = loan.getBook().getIsbn();
            this.loanDate = loan.getLoanDate().toString();
            this.expectedReturnDate = loan.getExpectedReturnDate().toString();
            this.actualReturnDate = loan.getActualReturnDate() != null ? 
                                   loan.getActualReturnDate().toString() : "-";
            this.status = loan.getStatus().name();
            this.penalty = String.format("$%.2f", loan.getPenalty());
        }
        
        public Long getLoanId() { return loanId; }
        public String getMemberName() { return memberName; }
        public String getMemberNumber() { return memberNumber; }
        public String getBookTitle() { return bookTitle; }
        public String getIsbn() { return isbn; }
        public String getLoanDate() { return loanDate; }
        public String getExpectedReturnDate() { return expectedReturnDate; }
        public String getActualReturnDate() { return actualReturnDate; }
        public String getStatus() { return status; }
        public String getPenalty() { return penalty; }
    }
    
    /**
     * Helper class for member combo box.
     */
    private static class MemberComboItem {
        private final Long id;
        private final String displayText;
        private final MembershipType membershipType;
        
        public MemberComboItem(Member member) {
            this.id = member.getId();
            this.displayText = member.getFullName() + " (" + member.getMemberNumber() + ")";
            this.membershipType = member.getMembershipType();
        }
        
        public Long getId() { return id; }
        public MembershipType getMembershipType() { return membershipType; }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
    
    /**
     * Helper class for book combo box.
     */
    private static class BookComboItem {
        private final Long id;
        private final String displayText;
        
        public BookComboItem(Book book) {
            this.id = book.getId();
            this.displayText = book.getTitle() + " - " + book.getAuthor() + 
                              " (Available: " + book.getAvailableStock() + ")";
        }
        
        public Long getId() { return id; }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
}