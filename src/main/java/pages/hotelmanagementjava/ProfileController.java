package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;


import java.sql.*;

public class ProfileController {

    @FXML private Button changepassword;
    @FXML private PasswordField confirmpassword;
    @FXML private TextField currentpassword;
    @FXML private Label email;
    @FXML private Label error;
    @FXML private Label firstname;
    @FXML private Label lastname;
    @FXML private PasswordField newpassword;
    @FXML private Label phonenumber;
    @FXML private Label username;

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
        if (!validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
            return;
        }

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Verify current password
            String checkSql = "SELECT * FROM admins WHERE username = ? AND password = ?";
            psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, username.getText());
            psCheck.setString(2, currentPassword);
            rs = psCheck.executeQuery();

            if (!rs.next()) {
                error.setText("Incorrect password");
                error.setTextFill(Color.RED);
                return;
            }

            // 2. Update password
            String updateSql = "UPDATE admins SET password = ? WHERE username = ?";
            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, newPassword);
            psUpdate.setString(2, username.getText());
            
            int rowsAffected = psUpdate.executeUpdate();
            
            if (rowsAffected > 0) {
                conn.commit(); // Commit transaction
                error.setText("Password changed successfully");
                error.setTextFill(Color.GREEN);
                clear();
                
                // Update the logged-in admin instance
                LoggedInAdmin.getInstance().setPassword(newPassword);
            } else {
                error.setText("Password update failed");
                error.setTextFill(Color.RED);
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            error.setText("Database error during password change");
            error.setTextFill(Color.RED);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseUtil.close(conn, psCheck, rs);
            DatabaseUtil.close(null, psUpdate, null);
        }
    }

    // Validate input for password change
    private boolean validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            error.setText("All password fields must be filled");
            error.setTextFill(Color.RED);
            return false;
        }

        if (newPassword.length() < 5) {
            error.setText("Password length must at least be 5");
            error.setTextFill(Color.RED);
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            error.setText("New passwords do not match");
            error.setTextFill(Color.RED);
            return false;
        }

        if (newPassword.equals(currentPassword)) {
            error.setText("New password must be different from current password");
            error.setTextFill(Color.RED);
            return false;
        }

        return true;
    }

    private void clear() {
        currentpassword.setText("");
        newpassword.setText("");
        confirmpassword.setText("");
    }
}