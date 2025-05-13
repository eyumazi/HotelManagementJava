package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import pages.hotelmanagementjava.classes.Admin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    @FXML
    private Button changepassword;

    @FXML
    private PasswordField confirmpassword;

    @FXML
    private TextField currentpassword;

    @FXML
    private Label email;

    @FXML
    private Label error;

    @FXML
    private Label firstname;

    @FXML
    private Label lastname;

    @FXML
    private PasswordField newpassword;

    @FXML
    private Label phonenumber;

    @FXML
    private Label username;

    @FXML
    private void initialize() {
        // Initialize the labels with the admin information
        LoggedInAdmin loggedInAdmin = LoggedInAdmin.getInstance();
        username.setText(loggedInAdmin.getUserName());
        firstname.setText(loggedInAdmin.getFirstName());
        lastname.setText(loggedInAdmin.getLastName());
        phonenumber.setText(loggedInAdmin.getAdminPhone());
        email.setText(loggedInAdmin.getAdminEmail());
    }
    @FXML
    private void changePassword() {
        String currentPassword = currentpassword.getText();
        String newPassword = newpassword.getText();
        String confirmPassword = confirmpassword.getText();

        // Validate input
        if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
            List<String> users = readUsersFromFile("data/admininfo.txt");
            for (int i = 0; i < users.size(); i++) {
                String[] parts = users.get(i).split("/");
                if (parts[2].equals(username.getText()) && parts[3].equals(currentPassword)) {
                    parts[3] = newPassword;
                    users.set(i, String.join("/", parts));
                    CheckinController.writeRoomFile("data/admininfo.txt" , users);
                    error.setText("Password changed successfully");
                    error.setTextFill(Color.GREEN);
                    clear();
                    return;
                }
            }

            error.setText("Incorrect password");
        }
    }
    public static List<String> readUsersFromFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    // Validate input for password change
    private boolean validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            error.setText("All password fields must be filled");
            return false;
        }

        if(newPassword.length() < 5){
            error.setText("Passowrd length must atleast be 5");
            return false;
        }


        if (!newPassword.equals(confirmPassword)) {
            error.setText("New passwords do not match");
            return false;
        }

        return true;
    }
    private void clear(){
        currentpassword.setText("");
        newpassword.setText("");
        confirmpassword.setText("");
    }

}
