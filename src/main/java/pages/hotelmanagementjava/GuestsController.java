package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import pages.hotelmanagementjava.classes.Guest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        loadDataFromFile("data/guestsinfo.txt");

        guestId.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is empty
            if (newValue.isEmpty()) {
                // Reload all guests into the table
                loadDataFromFile("data/guestsinfo.txt");
            }
        });
    }

    private void loadDataFromFile(String filePath) {
        List<Guest> allGuests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 5) {
                    int guestId = Integer.parseInt(parts[0]);
                    String firstName = parts[1];
                    String lastName = parts[2];
                    String phoneNumber = parts[3];
                    String email = parts[4];

                    Guest guest = new Guest(firstName, lastName, phoneNumber, email);
                    guest.setId(guestId);
                    allGuests.add(guest);
                }
            }

            displayGuests(allGuests);
        } catch (IOException e) {
            e.printStackTrace();
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
            int searchGuestId = Integer.parseInt(guestIdText);
            Guest foundGuest = findGuestById(searchGuestId);

            if (foundGuest != null) {
                displayGuests(List.of(foundGuest));
            } else {
                displayGuests(List.of());
            }
        }
    }

    private Guest findGuestById(int guestId) {
        for (Guest guest : guestsTable.getItems()) {
            if (guest.getId() == guestId) {
                return guest;
            }
        }
        return null;
    }
}
