package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pages.hotelmanagementjava.classes.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestsController {

    @FXML
    private TableView<Guest> guestsTable;

    @FXML
    private TableColumn<Guest, Integer> guestIdColumn;

    @FXML
    private TableColumn<Guest, String> fnameColumn;

    @FXML
    private TableColumn<Guest, String> lnameColumn;

    @FXML
    private TableColumn<Guest, String> phoneColumn;

    @FXML
    private TableColumn<Guest, String> emailColumn;

    @FXML
    private TextField guestId;

    public void initialize() {
        // Initialize the columns
        guestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load data from database
        loadDataFromDatabase();

        // Add listener for search functionality
        guestId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadDataFromDatabase();
            }
        });
    }

    private void loadDataFromDatabase() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Guest> allGuests = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT * FROM guests");
            rs = ps.executeQuery();

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
                allGuests.add(guest);
            }

            displayGuests(allGuests);
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error message to user if needed
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
    }

    private void displayGuests(List<Guest> guests) {
        guestsTable.getItems().clear();
        guestsTable.getItems().addAll(guests);
    }

    @FXML
    private void search() {
        String guestIdText = guestId.getText().trim();

        if (!guestIdText.isEmpty()) {
            try {
                int searchGuestId = Integer.parseInt(guestIdText);
                Guest foundGuest = Guest.getGuestById(searchGuestId);

                if (foundGuest != null) {
                    displayGuests(List.of(foundGuest));
                } else {
                    displayGuests(List.of());
                    // Optionally show "Guest not found" message
                }
            } catch (NumberFormatException e) {
                // Handle invalid number input
                displayGuests(List.of());
                // Optionally show error message
            }
        }
    }

    // Additional methods for CRUD operations could be added here
    // For example:
    @FXML
    private void addGuest() {
        // Implementation for adding a new guest
    }

    @FXML
    private void updateGuest() {
        // Implementation for updating a guest
    }

    @FXML
    private void deleteGuest() {
        // Implementation for deleting a guest
    }
}