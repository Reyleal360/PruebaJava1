package com.mycompany.booknova.ui;


import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.domain.Member.MembershipType;
import com.mycompany.booknova.service.MemberService;
import com.mycompany.booknova.service.impl.MemberServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 * Members management view.
 * Handles CRUD operations for library members.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MembersView {
    
    private final MemberService memberService;
    private final VBox mainLayout;
    private TableView<MemberTableModel> membersTable;
    private ObservableList<MemberTableModel> membersList;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    
    public MembersView() {
        this.memberService = new MemberServiceImpl();
        this.mainLayout = new VBox(15);
        this.membersList = FXCollections.observableArrayList();
        
        initializeView();
        loadMembers();
    }
    
    /**
     * Initializes the view components.
     */
    private void initializeView() {
        mainLayout.setPadding(new Insets(20));
        
        HBox header = createHeader();
        HBox searchBar = createSearchBar();
        membersTable = createMembersTable();
        VBox.setVgrow(membersTable, Priority.ALWAYS);
        HBox actionButtons = createActionButtons();
        
        mainLayout.getChildren().addAll(header, searchBar, membersTable, actionButtons);
    }
    
    /**
     * Creates the header section.
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("👥 Members Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addButton = new Button("+ Register New Member");
        addButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        );
        addButton.setOnAction(e -> showAddMemberDialog());
        
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
        searchField.setPromptText("Search by name, member number, or document ID...");
        searchField.setPrefWidth(350);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMembers());
        
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold;");
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Members", "Active", "Inactive");
        statusFilter.setValue("All Members");
        statusFilter.setOnAction(e -> filterMembers());
        
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Members");
            loadMembers();
        });
        
        searchBar.getChildren().addAll(
            searchLabel, searchField, statusLabel, 
            statusFilter, clearButton
        );
        
        return searchBar;
    }
    
    /**
     * Creates the members table.
     */
    private TableView<MemberTableModel> createMembersTable() {
        TableView<MemberTableModel> table = new TableView<>();
        table.setItems(membersList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<MemberTableModel, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<MemberTableModel, String> memberNumCol = new TableColumn<>("Member #");
        memberNumCol.setCellValueFactory(new PropertyValueFactory<>("memberNumber"));
        memberNumCol.setPrefWidth(150);
        
        TableColumn<MemberTableModel, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(200);
        
        TableColumn<MemberTableModel, String> documentCol = new TableColumn<>("Document ID");
        documentCol.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        documentCol.setPrefWidth(120);
        
        TableColumn<MemberTableModel, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);
        
        TableColumn<MemberTableModel, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);
        
        TableColumn<MemberTableModel, String> membershipCol = new TableColumn<>("Membership");
        membershipCol.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        membershipCol.setPrefWidth(100);
        
        TableColumn<MemberTableModel, String> registrationCol = new TableColumn<>("Registration Date");
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        registrationCol.setPrefWidth(130);
        
        TableColumn<MemberTableModel, Boolean> activeCol = new TableColumn<>("Status");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(80);
        activeCol.setCellFactory(column -> new TableCell<MemberTableModel, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Active" : "Inactive");
                    if (item) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        table.getColumns().addAll(
            idCol, memberNumCol, nameCol, documentCol, 
            emailCol, phoneCol, membershipCol, registrationCol, activeCol
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
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        viewButton.setOnAction(e -> viewMemberDetails());
        
        Button editButton = new Button("Edit Selected");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editButton.setOnAction(e -> editSelectedMember());
        
        Button toggleStatusButton = new Button("Toggle Status");
        toggleStatusButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        toggleStatusButton.setOnAction(e -> toggleMemberStatus());
        
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteSelectedMember());
        
        Button refreshButton = new Button("↻ Refresh");
        refreshButton.setOnAction(e -> loadMembers());
        
        buttonBox.getChildren().addAll(
            viewButton, editButton, toggleStatusButton, 
            deleteButton, refreshButton
        );
        return buttonBox;
    }
    
    /**
     * Loads all members from the service.
     */
    private void loadMembers() {
        try {
            membersList.clear();
            var members = memberService.getAllMembers();
            for (Member member : members) {
                membersList.add(new MemberTableModel(member));
            }
        } catch (Exception e) {
            showError("Error loading members: " + e.getMessage());
        }
    }
    
    /**
     * Filters members based on search criteria.
     */
    private void filterMembers() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = statusFilter.getValue();
            
            membersList.clear();
            var members = memberService.getAllMembers();
            
            for (Member member : members) {
                boolean matchesSearch = searchText.isEmpty() ||
                    member.getFullName().toLowerCase().contains(searchText) ||
                    member.getMemberNumber().toLowerCase().contains(searchText) ||
                    member.getDocumentId().toLowerCase().contains(searchText);
                
                boolean matchesStatus = selectedStatus.equals("All Members") ||
                    (selectedStatus.equals("Active") && member.isActive()) ||
                    (selectedStatus.equals("Inactive") && !member.isActive());
                
                if (matchesSearch && matchesStatus) {
                    membersList.add(new MemberTableModel(member));
                }
            }
        } catch (Exception e) {
            showError("Error filtering members: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog to add a new member.
     */
    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Register New Member");
        dialog.setHeaderText("Enter member information");
        
        ButtonType saveButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = createMemberFormGrid(null);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return extractMemberFromForm(grid, null);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(member -> {
            try {
                memberService.registerMember(member);
                showSuccess("Member registered successfully");
                loadMembers();
            } catch (Exception e) {
                showError("Error registering member: " + e.getMessage());
            }
        });
    }
    
    /**
     * Views details of the selected member.
     */
    private void viewMemberDetails() {
        MemberTableModel selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a member to view");
            return;
        }
        
        try {
            Member member = memberService.findMemberById(selected.getId());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Member Details");
            alert.setHeaderText(member.getFullName());
            
            String details = String.format(
                "Member Number: %s\n" +
                "Document ID: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Address: %s\n" +
                "Membership Type: %s (Max %d loans)\n" +
                "Registration Date: %s\n" +
                "Status: %s",
                member.getMemberNumber(),
                member.getDocumentId(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getMembershipType(),
                member.getMembershipType().getMaxLoans(),
                member.getRegistrationDate(),
                member.isActive() ? "Active" : "Inactive"
            );
            
            alert.setContentText(details);
            alert.showAndWait();
        } catch (Exception e) {
            showError("Error loading member details: " + e.getMessage());
        }
    }
    
    /**
     * Edits the selected member.
     */
    private void editSelectedMember() {
        MemberTableModel selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a member to edit");
            return;
        }
        
        try {
            Member member = memberService.findMemberById(selected.getId());
            showEditMemberDialog(member);
        } catch (Exception e) {
            showError("Error loading member: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog to edit a member.
     */
    private void showEditMemberDialog(Member member) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Update member information");
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        GridPane grid = createMemberFormGrid(member);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return extractMemberFromForm(grid, member);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedMember -> {
            try {
                memberService.updateMember(updatedMember);
                showSuccess("Member updated successfully");
                loadMembers();
            } catch (Exception e) {
                showError("Error updating member: " + e.getMessage());
            }
        });
    }
    
    /**
     * Creates the member form grid.
     */
    private GridPane createMemberFormGrid(Member member) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField firstNameField = new TextField(member != null ? member.getFirstName() : "");
        firstNameField.setPromptText("First Name");
        firstNameField.setUserData("firstName");
        
        TextField lastNameField = new TextField(member != null ? member.getLastName() : "");
        lastNameField.setPromptText("Last Name");
        lastNameField.setUserData("lastName");
        
        TextField documentIdField = new TextField(member != null ? member.getDocumentId() : "");
        documentIdField.setPromptText("Document ID");
        documentIdField.setUserData("documentId");
        
        TextField emailField = new TextField(member != null ? member.getEmail() : "");
        emailField.setPromptText("Email");
        emailField.setUserData("email");
        
        TextField phoneField = new TextField(member != null ? member.getPhone() : "");
        phoneField.setPromptText("Phone");
        phoneField.setUserData("phone");
        
        TextField addressField = new TextField(member != null ? member.getAddress() : "");
        addressField.setPromptText("Address");
        addressField.setUserData("address");
        
        ComboBox<MembershipType> membershipCombo = new ComboBox<>();
        membershipCombo.getItems().addAll(MembershipType.values());
        membershipCombo.setValue(member != null ? member.getMembershipType() : MembershipType.BASIC);
        membershipCombo.setUserData("membershipType");
        
        CheckBox activeCheck = new CheckBox("Active");
        activeCheck.setSelected(member == null || member.isActive());
        activeCheck.setUserData("active");
        
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Document ID:"), 0, 2);
        grid.add(documentIdField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Membership:"), 0, 6);
        grid.add(membershipCombo, 1, 6);
        grid.add(activeCheck, 1, 7);
        
        return grid;
    }
    
    /**
     * Extracts member data from the form grid.
     */
    private Member extractMemberFromForm(GridPane grid, Member existingMember) {
        Member member = existingMember != null ? existingMember : new Member();
        
        for (var node : grid.getChildren()) {
            if (node.getUserData() != null) {
                String fieldName = node.getUserData().toString();
                switch (fieldName) {
                    case "firstName":
                        member.setFirstName(((TextField) node).getText());
                        break;
                    case "lastName":
                        member.setLastName(((TextField) node).getText());
                        break;
                    case "documentId":
                        member.setDocumentId(((TextField) node).getText());
                        break;
                    case "email":
                        member.setEmail(((TextField) node).getText());
                        break;
                    case "phone":
                        member.setPhone(((TextField) node).getText());
                        break;
                    case "address":
                        member.setAddress(((TextField) node).getText());
                        break;
                    case "membershipType":
                        member.setMembershipType(((ComboBox<MembershipType>) node).getValue());
                        break;
                    case "active":
                        member.setActive(((CheckBox) node).isSelected());
                        break;
                }
            }
        }
        
        if (existingMember == null) {
            member.setRegistrationDate(LocalDate.now());
        }
        
        return member;
    }
    
    /**
     * Toggles the status of the selected member.
     */
    private void toggleMemberStatus() {
        MemberTableModel selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a member");
            return;
        }
        
        try {
            if (selected.getActive()) {
                memberService.deactivateMember(selected.getId());
                showSuccess("Member deactivated successfully");
            } else {
                memberService.activateMember(selected.getId());
                showSuccess("Member activated successfully");
            }
            loadMembers();
        } catch (Exception e) {
            showError("Error toggling member status: " + e.getMessage());
        }
    }
    
    /**
     * Deletes the selected member.
     */
    private void deleteSelectedMember() {
        MemberTableModel selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a member to delete");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Member");
        confirmAlert.setContentText("Are you sure you want to delete: " + selected.getFullName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    memberService.deleteMember(selected.getId());
                    showSuccess("Member deleted successfully");
                    loadMembers();
                } catch (Exception e) {
                    showError("Error deleting member: " + e.getMessage());
                }
            }
        });
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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
     * Model class for displaying members in the table.
     */
    public static class MemberTableModel {
        private final Long id;
        private final String memberNumber;
        private final String fullName;
        private final String documentId;
        private final String email;
        private final String phone;
        private final String membershipType;
        private final String registrationDate;
        private final Boolean active;
        
        public MemberTableModel(Member member) {
            this.id = member.getId();
            this.memberNumber = member.getMemberNumber();
            this.fullName = member.getFullName();
            this.documentId = member.getDocumentId();
            this.email = member.getEmail();
            this.phone = member.getPhone();
            this.membershipType = member.getMembershipType().name();
            this.registrationDate = member.getRegistrationDate().toString();
            this.active = member.isActive();
        }
        
        public Long getId() { return id; }
        public String getMemberNumber() { return memberNumber; }
        public String getFullName() { return fullName; }
        public String getDocumentId() { return documentId; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getMembershipType() { return membershipType; }
        public String getRegistrationDate() { return registrationDate; }
        public Boolean getActive() { return active; }
    }
}