package pages.hotelmanagementjava;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pages.hotelmanagementjava.classes.Admin;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Define the columns for the TableView
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

        // Load  data from the file and populate the TableView
        usersList = FXCollections.observableArrayList(readUsersFromFile("data/admininfo.txt"));
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
            // Create a new User object
            Admin newUser = new Admin(firstName, lastName, phoneNumber, email, username, password);

            // Add the new user to the TableView and update the file
            usersList.add(newUser);
            writeUserToFile(newUser, "data/admininfo.txt");

            // Clear input fields and error label
            clearFields();
            errorLabel.setText("");
        }
    }

    private boolean validateInput(String firstName, String lastName, String phoneNumber,
                                  String email, String username, String password, String confirmPassword) {

        // Sample validation for non-empty fields
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() ||
                email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields must be filled");
            return false;
        }

        if (password.length() < 5){
            errorLabel.setText("Password length must be atleast 5");
            return false;
        }

        // Sample validation for password match
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return false;
        }

        return true;
    }

    private void writeUserToFile(Admin user, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(user.getFirstName() + "/" + user.getLastName() + "/" + user.getUsername() + "/"
                    + user.getPassword() + "/" + user.getPhoneNumber() + "/" + user.getEmail()+ "\n") ;
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Admin> readUsersFromFile(String filePath) {
        ObservableList<Admin> users = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 6) {
                    String firstName = parts[0].trim();
                    String lastName = parts[1].trim();
                    String username = parts[2].trim();
                    String phoneNumber = parts[4].trim();
                    String email = parts[5].trim();
                    String password = parts[3];

                    Admin user = new Admin(firstName, lastName, phoneNumber, email, username,password);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Please try again later!");
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
