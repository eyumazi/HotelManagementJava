package pages.hotelmanagementjava;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import pages.hotelmanagementjava.classes.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingsController {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> guestIdColumn;
    @FXML private TableColumn<Booking, String> roomNumberColumn;
    @FXML private TableColumn<Booking, String> checkInDateColumn;
    @FXML private TableColumn<Booking, String> checkOutDateColumn;
    @FXML private TextField guestIdFilter;
    @FXML private TextField roomNumberFilter;
    @FXML private Label checkinDate;
    @FXML private Label checkoutDate;
    @FXML private Label error;
    @FXML private Label guestName;
    @FXML private Label roomNumber;
    @FXML private Label totalPrice;

    private List<Booking> allBookings;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initialize() {
        // Initialize the columns
        guestIdColumn.setCellValueFactory(new PropertyValueFactory<>("guestId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // Load data from database
        loadDataFromDatabase();

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLabels(newSelection);
            }
        });
    }

    private void loadDataFromDatabase() {
        allBookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT * FROM bookings");
            rs = ps.executeQuery();

            while (rs.next()) {
                int guestId = rs.getInt("guestId");
                String roomNum = String.valueOf(rs.getInt("roomNumber"));
                String checkIn = rs.getString("checkInDate");
                String checkOut = rs.getString("checkOutDate");

                Booking booking = new Booking(guestId, roomNum, checkIn, checkOut);
                allBookings.add(booking);
            }
            displayBookings(allBookings);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
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
            totalPrice.setText(findPrice(selectedBooking.getRoomNumber(), 
                                      selectedBooking.getCheckInDate(), 
                                      selectedBooking.getCheckOutDate()));
        }
    }

    private String findPrice(String roomNumber, String checkindate, String checkoutdate) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT price FROM rooms WHERE roomNumber = ?");
            ps.setInt(1, Integer.parseInt(roomNumber));
            rs = ps.executeQuery();
            
            if (rs.next()) {
                double price = rs.getDouble("price");
                double totalPrice = calculatePrice(price, checkindate, checkoutdate);
                return String.valueOf(totalPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        return "0.0";
    }

    private String getGuestName(int guestId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT firstName, lastName FROM guests WHERE id = ?");
            ps.setInt(1, guestId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("firstName") + " " + rs.getString("lastName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        return "Guest Not Found";
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
        displayBookings(filteredBookings);
    }

    private boolean matchesFilter(Booking booking, String guestIdFilter, String roomNumberFilter) {
        return (guestIdFilter.isEmpty() || String.valueOf(booking.getGuestId()).contains(guestIdFilter)) &&
               (roomNumberFilter.isEmpty() || String.valueOf(booking.getRoomNumber()).contains(roomNumberFilter));
    }

    public static double calculatePrice(double price, String checkinDate, String checkoutDate) {
        LocalDate startDate = LocalDate.parse(checkinDate, dateFormatter);
        LocalDate endDate = LocalDate.parse(checkoutDate, dateFormatter);
        int days = (int) startDate.until(endDate).getDays();
        return price * days;
    }

    @FXML
    private void checkOut() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            error.setText("Please select a booking.");
            return;
        }

        Connection conn = null;
        PreparedStatement psDeleteBooking = null;
        PreparedStatement psUpdateRoom = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Delete booking
            psDeleteBooking = conn.prepareStatement("DELETE FROM bookings WHERE guestId = ? AND roomNumber = ?");
            psDeleteBooking.setInt(1, selectedBooking.getGuestId());
            psDeleteBooking.setInt(2, Integer.parseInt(selectedBooking.getRoomNumber()));
            psDeleteBooking.executeUpdate();
            
            // Update room availability
            psUpdateRoom = conn.prepareStatement("UPDATE rooms SET availability = true WHERE roomNumber = ?");
            psUpdateRoom.setInt(1, Integer.parseInt(selectedBooking.getRoomNumber()));
            psUpdateRoom.executeUpdate();
            
            conn.commit(); // Commit transaction
            
            error.setText("Checkout Successfully!");
            error.setTextFill(Color.GREEN);
            loadDataFromDatabase();
            clearDetailsLabels();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            error.setText("Error during checkout");
            error.setTextFill(Color.RED);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseUtil.close(conn, psDeleteBooking, null);
            DatabaseUtil.close(null, psUpdateRoom, null);
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