package pages.hotelmanagementjava;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.io.IOException;
public class HomePageController {


    @FXML
    private AnchorPane mainpart;

    @FXML
    private Label pagetitle;
    @FXML
    private void loadProfile() {
        loadContent("profile.fxml", "Profile Page");
    }

    @FXML
    private void loadUsers() {
        loadContent("addusers.fxml", "Users Page");
    }

    @FXML
    private void loadCheckIn() {
        loadContent("checkin.fxml", "Check In Page");
    }

    @FXML
    private void loadCheckOut() {
        loadContent("checkout.fxml", "Check Out Page");
    }

    @FXML
    private void loadBookings() {
        loadContent("bookings.fxml", "Bookings Page");
    }

    @FXML
    private void loadGuests() {
        loadContent("guests.fxml", "Guests Page");
    }

    @FXML
    private void loadRooms() {
        loadContent("rooms.fxml", "Rooms Page");
    }
    @FXML
    private void loadLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Pane loginPage = loader.load();
            mainpart.getScene().setRoot(loginPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFileName, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Pane content = loader.load();
            mainpart.getChildren().setAll(content);
            pagetitle.setText(pageTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}