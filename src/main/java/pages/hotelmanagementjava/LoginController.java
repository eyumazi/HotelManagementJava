package pages.hotelmanagementjava;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class LoginController {

    @FXML
    private Label error;

    @FXML
    private Button loginbtn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    public void goToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homepage.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void login(ActionEvent event) throws IOException {
        String enteredUsername = username.getText();
        String enteredPassword = password.getText();
        
        if (isValidCredentials(enteredUsername, enteredPassword)) {
            goToHome(event);
        } else {
            error.setText("Incorrect Username or Password");
        }
    }

    private boolean isValidCredentials(String enteredUsername, String enteredPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT * FROM admins WHERE username = ? AND password = ?");
            ps.setString(1, enteredUsername);
            ps.setString(2, enteredPassword);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                // If we found a matching admin, set their info
                String[] adminInfo = new String[6];
                adminInfo[0] = rs.getString("firstName");
                adminInfo[1] = rs.getString("lastName");
                adminInfo[2] = rs.getString("username");
                adminInfo[3] = rs.getString("password");
                adminInfo[4] = rs.getString("phone");
                adminInfo[5] = rs.getString("email");
                
                setLoggedInAdminInfo(adminInfo);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error.setText("Database error occurred");
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        return false;
    }

    private void setLoggedInAdminInfo(String[] adminInfo) {
        LoggedInAdmin loggedInAdmin = LoggedInAdmin.getInstance();
        loggedInAdmin.setFirstName(adminInfo[0]);
        loggedInAdmin.setLastName(adminInfo[1]);
        loggedInAdmin.setUserName(adminInfo[2]);
        loggedInAdmin.setPassword(adminInfo[3]);
        loggedInAdmin.setAdminPhone(adminInfo[4]);
        loggedInAdmin.setAdminEmail(adminInfo[5]);
    }
}