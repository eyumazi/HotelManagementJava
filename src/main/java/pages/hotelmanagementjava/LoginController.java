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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    public void login (ActionEvent event) throws IOException {
        String enteredUsername = username.getText();
        String enteredPassword = password.getText();
        if (isValidCredentials(enteredUsername, enteredPassword)) {
            goToHome(event);
        } else {
            error.setText("Incorrect Username or Password");
        }
    }
    private boolean isValidCredentials(String enteredUsername, String enteredPassword) {
        String filePath = "data/admininfo.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] adminInfo = line.split("/");
                if (adminInfo.length >= 4) {
                    String storedUsername = adminInfo[2];
                    String storedPassword = adminInfo[3];

                    if (enteredUsername.equals(storedUsername) && enteredPassword.equals(storedPassword)) {
                        setLoggedInAdminInfo(adminInfo);
                        return true;
                    }
                } else {
                    System.out.println("Invalid format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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