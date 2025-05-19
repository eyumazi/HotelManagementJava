package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import pages.hotelmanagementjava.classes.Booking;
import pages.hotelmanagementjava.classes.Guest;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckinController {

    // UI components (same as original)
    @FXML private ToggleGroup capacity;
    @FXML private DatePicker checkindate;
    @FXML private DatePicker checkoutdate;
    @FXML private RadioButton doubleRb;
    @FXML private RadioButton economyRb;
    @FXML private TextField email;
    @FXML private Label error;
    @FXML private TextField firstname;
    @FXML private TextField lastname;
    @FXML private RadioButton normalRb;
    @FXML private TextField phonenumber;
    @FXML private RadioButton singleRb;
    @FXML private RadioButton tripleRb;
    @FXML private ToggleGroup type;
    @FXML private RadioButton vipRb;
    @FXML private Label price;
    @FXML private Label roomNumber;

    private boolean found = false;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Validation methods (same as original)
    private boolean validateCustomerData() {
        if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || 
            phonenumber.getText().isEmpty() || email.getText().isEmpty()) {
            showError("All fields must be filled.");
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (!email.getText().matches(emailPattern)) {
            showError("Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private boolean validateRoomAndDates() {
        if (type.getSelectedToggle() == null || capacity.getSelectedToggle() == null ||
            checkindate.getValue() == null || checkoutdate.getValue() == null) {
            showError("Please select room type, capacity, and enter check-in and check-out dates.");
            return false;
        }

        if (checkoutdate.getValue().isBefore(checkindate.getValue())) {
            showError("Check-out date cannot be before check-in date.");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        error.setText(message);
        error.setTextFill(Color.RED);
    }

    private void clearError() {
        error.setText("");
    }

    @FXML
    private void submit() {
        clearError();

        if (!validateCustomerData() || !validateRoomAndDates()) {
            return;
        }

        String roomType = getSelectedRadioButton(type);
        String roomCapacity = getSelectedRadioButton(capacity);

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM rooms WHERE roomType = ? AND roomCapacity = ? AND availability = true LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, roomType);
            ps.setString(2, roomCapacity);
            rs = ps.executeQuery();

            if (rs.next()) {
                int availableRoomNumber = rs.getInt("roomNumber");
                double roomPrice = rs.getDouble("price");
                
                roomNumber.setText(String.valueOf(availableRoomNumber));
                double totalPrice = calculatePrice(roomPrice, 
                    checkindate.getValue().toString(), 
                    checkoutdate.getValue().toString());
                price.setText(String.valueOf(totalPrice));
                found = true;
            } else {
                showError("No available room matching your criteria.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error accessing room information. Please try again.");
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
    }

    private double calculatePrice(double price, String checkinDate, String checkoutDate) {
        LocalDate startDate = LocalDate.parse(checkinDate, dateFormatter);
        LocalDate endDate = LocalDate.parse(checkoutDate, dateFormatter);
        return price * startDate.until(endDate).getDays();
    }

    private String getSelectedRadioButton(ToggleGroup toggleGroup) {
        return ((RadioButton) toggleGroup.getSelectedToggle()).getText();
    }

    @FXML
    private void confirm() {
        if (validateCustomerData() && validateRoomAndDates() && found) {
            Connection conn = null;
            try {
                conn = DatabaseUtil.getConnection();
                conn.setAutoCommit(false); // Start transaction

                // Create guest
                Guest guest = new Guest(
                    firstname.getText(),
                    lastname.getText(),
                    phonenumber.getText(),
                    email.getText()
                );
                guest.setId();
                guest.saveGuest();

                // Create booking
                Booking booking = new Booking(
                    guest.getId(),
                    roomNumber.getText(),
                    checkindate.getValue().toString(),
                    checkoutdate.getValue().toString()
                );
                booking.addBooking();

                // Update room availability
                updateRoomAvailability(Integer.parseInt(roomNumber.getText()), false);

                conn.commit(); // Commit transaction

                showSuccess("Check-in has been successful!");
                clear();
            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                showError("Error during check-in. Please try again.");
            } finally {
                try {
                    if (conn != null) conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showError("Confirmation Unsuccessful!");
        }
    }

    private void updateRoomAvailability(int roomNumber, boolean available) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE rooms SET availability = ? WHERE roomNumber = ?";
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, available);
            ps.setInt(2, roomNumber);
            ps.executeUpdate();
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(message);
        alert.setContentText("Thank you for choosing our hotel.");
        alert.showAndWait();
    }

    @FXML
    private void clear() {
        clearError();
        firstname.clear();
        lastname.clear();
        phonenumber.clear();
        email.clear();
        type.selectToggle(null);
        capacity.selectToggle(null);
        checkindate.getEditor().clear();
        checkoutdate.getEditor().clear();
        roomNumber.setText("");
        price.setText("");
        found = false;
    }
}