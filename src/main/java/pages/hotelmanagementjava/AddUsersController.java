package pages.hotelmanagementjava;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pages.hotelmanagementjava.classes.Admin;
import java.sql.*;

public class AddUsersController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button addUserButton;

    @FXML
    private Label errorLabel;

    @FXML
    private TableView<Admin> userTableView;

    @FXML
    private TableColumn<Admin, String> firstNameColumn;
    @FXML
    private TableColumn<Admin, String> lastNameColumn;
    @FXML
    private TableColumn<Admin, String> usernameColumn;
    @FXML
    private TableColumn<Admin, String> phoneNumberColumn;
    @FXML
    private TableColumn<Admin, String> emailColumn;

    private ObservableList<Admin> usersList;

    @FXML
    private void initialize() {
        // Initialize the TableView columns
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load data from database and populate the TableView
        usersList = FXCollections.observableArrayList(loadUsersFromDatabase());
        userTableView.setItems(usersList);
    }

    @FXML
    private void addUser() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (validateInput(firstName, lastName, phoneNumber, email, username, password, confirmPassword)) {
            // Create a new Admin object
            Admin newUser = new Admin(firstName, lastName, phoneNumber, email, username, password);

            // Add the new user to the database and TableView
            if (addUserToDatabase(newUser)) {
                usersList.add(newUser);
                clearFields();
                errorLabel.setText("");
            } else {
                errorLabel.setText("Failed to add user. Username might already exist.");
            }
        }
    }

    private boolean validateInput(String firstName, String lastName, String phoneNumber,
                                 String email, String username, String password, String confirmPassword) {

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() ||
                email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields must be filled");
            return false;
        }

        if (password.length() < 5) {
            errorLabel.setText("Password length must be at least 5");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean addUserToDatabase(Admin user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO admins (firstName, lastName, username, password, phone, email) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }

    private ObservableList<Admin> loadUsersFromDatabase() {
        ObservableList<Admin> users = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM admins");
            
            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                
                Admin user = new Admin(firstName, lastName, phone, email, username, password);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading users from database");
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return users;
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        phoneNumberField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}