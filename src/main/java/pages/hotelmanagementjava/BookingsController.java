package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pages.hotelmanagementjava.classes.Booking;


import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingsController {

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, Integer> guestIdColumn;

    @FXML
    private TableColumn<Booking, String> roomNumberColumn;

    @FXML
    private TableColumn<Booking, String> checkInDateColumn;

    @FXML
    private TableColumn<Booking, String> checkOutDateColumn;

    @FXML
    private TextField guestIdFilter;

    @FXML
    private TextField roomNumberFilter;

    private List<Booking> allBookings;


    @FXML
    private Label checkinDate;

    @FXML
    private Label checkoutDate;

    @FXML
    private Label error;

    @FXML
    private Label guestName;

    @FXML
    private Label roomNumber;

    @FXML
    private Label totalPrice;

    public void initialize() {
        // Initialize the columns
        guestIdColumn.setCellValueFactory(new PropertyValueFactory<>("guestId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // Load data from the booking.txt file and display it in the table
        loadDataFromFile("data/booking.txt");

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLabels(newSelection);
            }
        });
    }
    private void updateLabels(Booking selectedBooking) {
        if (guestName != null) {
            guestName.setText(getGuestName(selectedBooking.getGuestId()));
        }

        if (roomNumber != null) {
            roomNumber.setText(selectedBooking.getRoomNumber());
        }

        if (checkinDate != null) {
            checkinDate.setText(selectedBooking.getCheckInDate());
        }

        if (checkoutDate != null) {
            checkoutDate.setText(selectedBooking.getCheckOutDate());
        }

        if (totalPrice != null) {
            totalPrice.setText(findPrice(selectedBooking.getRoomNumber(), selectedBooking.getCheckInDate(), selectedBooking.getCheckOutDate()));
        }
    }

    private String findPrice (String roomNumber , String checkindate , String checkoutdate){
        String filePath = "data/room.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 5 && parts[0].equalsIgnoreCase(roomNumber)) {
                    try {
                        double Price = Double.parseDouble(parts[3]);
                        double totalPrice = calculatePrice( Price , checkindate, checkoutdate);
                        String total = String.valueOf(totalPrice);
                        return total;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid double format");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    private String getGuestName(int guestId) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/guestsinfo.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 5) {
                    int currentGuestId = Integer.parseInt(parts[0]);
                    if (currentGuestId == guestId) {
                        // Concatenate first name and last name
                        return parts[1] + " " + parts[2];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Guest Not Found";
    }
    private void loadDataFromFile(String filePath) {
        allBookings = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 4) {
                    int guestId = Integer.parseInt(parts[0]);
                    String roomNumber = parts[1];
                    String checkInDate = parts[2];
                    String checkOutDate = parts[3];

                    Booking booking = new Booking(guestId, roomNumber, checkInDate, checkOutDate);
                    allBookings.add(booking);
                }
            }
            displayBookings(allBookings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayBookings(List<Booking> bookings) {
        bookingsTable.getItems().clear();
        bookingsTable.getItems().addAll(bookings);
    }

    @FXML
    private void filter() {
        String guestIdFilterText = guestIdFilter.getText().trim();
        String roomNumberFilterText = roomNumberFilter.getText().trim();

        List<Booking> filteredBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (matchesFilter(booking, guestIdFilterText, roomNumberFilterText)) {
                filteredBookings.add(booking);
            }
        }

        // Display filtered bookings in the table
        displayBookings(filteredBookings);
    }

    private boolean matchesFilter(Booking booking, String guestIdFilter, String roomNumberFilter) {
        return (guestIdFilter.isEmpty() || String.valueOf(booking.getGuestId()).contains(guestIdFilter)) &&
                (roomNumberFilter.isEmpty() || String.valueOf(booking.getRoomNumber()).contains(roomNumberFilter));
    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static double calculatePrice(double price, String checkinDate, String checkoutDate) {
        LocalDate startDate = LocalDate.parse(checkinDate, dateFormatter);
        LocalDate endDate = LocalDate.parse(checkoutDate, dateFormatter);

        int days = (int) startDate.until(endDate).getDays();

        // Assuming price is per day
        double totalPrice = price * days;

        return totalPrice;
    }

    @FXML
    private void checkOut() {
        // Retrieve selected booking
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            error.setText("Please select a booking.");
            return;
        }
        deleteBookingLine("data/booking.txt", selectedBooking.getGuestId());
        List<String> lines = CheckinController.readRoomFile("data/room.txt");
        CheckinController.updateAvailability(lines, Integer.parseInt(roomNumber.getText()), true);
        CheckinController.writeRoomFile("data/room.txt", lines);
        error.setText("Checkout Successfully!");
        error.setTextFill(Color.GREEN);
        loadDataFromFile("data/booking.txt");
        clearDetailsLabels();
    }


    private void deleteBookingLine(String filePath, int guestIdToDelete) {
        // Read all lines from the file
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length >= 1) {
                    int guestId = Integer.parseInt(parts[0].trim());
                    // Skip the line with the matching guestId
                    if (guestId != guestIdToDelete) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the modified lines back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearDetailsLabels() {
        guestName.setText("");
        roomNumber.setText("");
        checkinDate.setText("");
        checkoutDate.setText("");
        totalPrice.setText("");
    }
}
