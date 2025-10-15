package com.mycompany.booknova.ui;

import com.mycompany.booknova.domain.Book;
import com.mycompany.booknova.service.BookService;
import com.mycompany.booknova.service.impl.BookServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Books management view.
 * Handles CRUD operations for books with search and filter capabilities.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class BooksView {
    
    private final BookService bookService;
    private final VBox mainLayout;
    private TableView<BookTableModel> booksTable;
    private ObservableList<BookTableModel> booksList;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    
    public BooksView() {
        this.bookService = new BookServiceImpl();
        this.mainLayout = new VBox(15);
        this.booksList = FXCollections.observableArrayList();
        
        initializeView();
        loadBooks();
    }
    
    /**
     * Initializes the view components.
     */
    private void initializeView() {
        mainLayout.setPadding(new Insets(20));
        
        // Header
        HBox header = createHeader();
        
        // Search and filters
        HBox searchBar = createSearchBar();
        
        // Books table
        booksTable = createBooksTable();
        VBox.setVgrow(booksTable, Priority.ALWAYS);
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        mainLayout.getChildren().addAll(header, searchBar, booksTable, actionButtons);
    }
    
    /**
     * Creates the header section.
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📖 Books Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addButton = new Button("+ Add New Book");
        addButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        );
        addButton.setOnAction(e -> showAddBookDialog());
        
        header.getChildren().addAll(title, spacer, addButton);
        return header;
    }
    
    /**
     * Creates the search bar with filters.
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label searchLabel = new Label("🔍");
        searchLabel.setStyle("-fx-font-size: 16px;");
        
        searchField = new TextField();
        searchField.setPromptText("Search by title, author, or ISBN...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBooks());
        
        Label categoryLabel = new Label("Category:");
        categoryLabel.setStyle("-fx-font-weight: bold;");
        
        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll(
            "All Categories", "Fiction", "Non-Fiction", "Science", 
            "Technology", "History", "Biography", "Children"
        );
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(e -> filterBooks());
        
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> {
            searchField.clear();
            categoryFilter.setValue("All Categories");
            loadBooks();
        });
        
        searchBar.getChildren().addAll(
            searchLabel, searchField, categoryLabel, 
            categoryFilter, clearButton
        );
        
        return searchBar;
    }
    
    /**
     * Creates the books table.
     */
    private TableView<BookTableModel> createBooksTable() {
        TableView<BookTableModel> table = new TableView<>();
        table.setItems(booksList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // ID Column
        TableColumn<BookTableModel, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        // ISBN Column
        TableColumn<BookTableModel, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);
        
        // Title Column
        TableColumn<BookTableModel, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        
        // Author Column
        TableColumn<BookTableModel, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(180);
        
        // Category Column
        TableColumn<BookTableModel, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);
        
        // Available Stock Column
        TableColumn<BookTableModel, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableStock"));
        availableCol.setPrefWidth(80);
        availableCol.setCellFactory(column -> new TableCell<BookTableModel, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item == 0) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (item < 3) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Total Stock Column
        TableColumn<BookTableModel, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        totalCol.setPrefWidth(70);
        
        // Status Column
        TableColumn<BookTableModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            int available = cellData.getValue().getAvailableStock();
            return new SimpleStringProperty(available > 0 ? "Available" : "Out of Stock");
        });
        statusCol.setPrefWidth(100);
        
        table.getColumns().addAll(
            idCol, isbnCol, titleCol, authorCol, 
            categoryCol, availableCol, totalCol, statusCol
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
        
        Button editButton = new Button("Edit Selected");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editButton.setOnAction(e -> editSelectedBook());
        
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteSelectedBook());
        
        Button refreshButton = new Button("↻ Refresh");
        refreshButton.setOnAction(e -> loadBooks());
        
        buttonBox.getChildren().addAll(editButton, deleteButton, refreshButton);
        return buttonBox;
    }
    
    /**
     * Loads all books from the service.
     */
    private void loadBooks() {
        try {
            booksList.clear();
            var books = bookService.getAllBooks();
            for (Book book : books) {
                booksList.add(new BookTableModel(book));
            }
        } catch (Exception e) {
            showError("Error loading books: " + e.getMessage());
        }
    }
    
    /**
     * Filters books based on search criteria.
     */
    private void filterBooks() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String selectedCategory = categoryFilter.getValue();
            
            booksList.clear();
            var books = bookService.getAllBooks();
            
            for (Book book : books) {
                boolean matchesSearch = searchText.isEmpty() ||
                    book.getTitle().toLowerCase().contains(searchText) ||
                    book.getAuthor().toLowerCase().contains(searchText) ||
                    book.getIsbn().toLowerCase().contains(searchText);
                
                boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    book.getCategory().equalsIgnoreCase(selectedCategory);
                
                if (matchesSearch && matchesCategory) {
                    booksList.add(new BookTableModel(book));
                }
            }
        } catch (Exception e) {
            showError("Error filtering books: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog to add a new book.
     */
    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book information");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        
        TextField publisherField = new TextField();
        publisherField.setPromptText("Publisher");
        
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
            "Fiction", "Non-Fiction", "Science", "Technology", 
            "History", "Biography", "Children"
        );
        
        TextField totalStockField = new TextField();
        totalStockField.setPromptText("Total Stock");
        
        TextField availableStockField = new TextField();
        availableStockField.setPromptText("Available Stock");
        
        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryCombo, 1, 5);
        grid.add(new Label("Total Stock:"), 0, 6);
        grid.add(totalStockField, 1, 6);
        grid.add(new Label("Available Stock:"), 0, 7);
        grid.add(availableStockField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Book book = new Book();
                    book.setIsbn(isbnField.getText());
                    book.setTitle(titleField.getText());
                    book.setAuthor(authorField.getText());
                    book.setPublisher(publisherField.getText());
                    book.setPublicationYear(Integer.parseInt(yearField.getText()));
                    book.setCategory(categoryCombo.getValue());
                    book.setTotalStock(Integer.parseInt(totalStockField.getText()));
                    book.setAvailableStock(Integer.parseInt(availableStockField.getText()));
                    return book;
                } catch (NumberFormatException e) {
                    showError("Invalid number format");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(book -> {
            try {
                bookService.createBook(book);
                showSuccess("Book added successfully");
                loadBooks();
            } catch (Exception e) {
                showError("Error adding book: " + e.getMessage());
            }
        });
    }
    
    /**
     * Edits the selected book.
     */
    private void editSelectedBook() {
        BookTableModel selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a book to edit");
            return;
        }
        
        try {
            Book book = bookService.findBookById(selected.getId());
            showEditBookDialog(book);
        } catch (Exception e) {
            showError("Error loading book: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog to edit a book.
     */
    private void showEditBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Update book information");
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField isbnField = new TextField(book.getIsbn());
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField publisherField = new TextField(book.getPublisher());
        TextField yearField = new TextField(String.valueOf(book.getPublicationYear()));
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
            "Fiction", "Non-Fiction", "Science", "Technology", 
            "History", "Biography", "Children"
        );
        categoryCombo.setValue(book.getCategory());
        
        TextField totalStockField = new TextField(String.valueOf(book.getTotalStock()));
        TextField availableStockField = new TextField(String.valueOf(book.getAvailableStock()));
        
        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryCombo, 1, 5);
        grid.add(new Label("Total Stock:"), 0, 6);
        grid.add(totalStockField, 1, 6);
        grid.add(new Label("Available Stock:"), 0, 7);
        grid.add(availableStockField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    book.setIsbn(isbnField.getText());
                    book.setTitle(titleField.getText());
                    book.setAuthor(authorField.getText());
                    book.setPublisher(publisherField.getText());
                    book.setPublicationYear(Integer.parseInt(yearField.getText()));
                    book.setCategory(categoryCombo.getValue());
                    book.setTotalStock(Integer.parseInt(totalStockField.getText()));
                    book.setAvailableStock(Integer.parseInt(availableStockField.getText()));
                    return book;
                } catch (NumberFormatException e) {
                    showError("Invalid number format");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedBook -> {
            try {
                bookService.updateBook(updatedBook);
                showSuccess("Book updated successfully");
                loadBooks();
            } catch (Exception e) {
                showError("Error updating book: " + e.getMessage());
            }
        });
    }
    
    /**
     * Deletes the selected book.
     */
    private void deleteSelectedBook() {
        BookTableModel selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a book to delete");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Book");
        confirmAlert.setContentText("Are you sure you want to delete: " + selected.getTitle() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bookService.deleteBook(selected.getId());
                    showSuccess("Book deleted successfully");
                    loadBooks();
                } catch (Exception e) {
                    showError("Error deleting book: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Shows a success message.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows a warning message.
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an error message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Returns the main view layout.
     */
    public VBox getView() {
        return mainLayout;
    }
    
    /**
     * Model class for displaying books in the table.
     */
    public static class BookTableModel {
        private final Long id;
        private final String isbn;
        private final String title;
        private final String author;
        private final String category;
        private final Integer availableStock;
        private final Integer totalStock;
        
        public BookTableModel(Book book) {
            this.id = book.getId();
            this.isbn = book.getIsbn();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.category = book.getCategory();
            this.availableStock = book.getAvailableStock();
            this.totalStock = book.getTotalStock();
        }
        
        public Long getId() { return id; }
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCategory() { return category; }
        public Integer getAvailableStock() { return availableStock; }
        public Integer getTotalStock() { return totalStock; }
    }
}