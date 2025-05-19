package pages.hotelmanagementjava.classes;

import pages.hotelmanagementjava.DatabaseUtil;
import java.sql.*;

public class Booking {
    private int guestId;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;

    // Constructors
    public Booking(int guestId, String roomNumber, String checkInDate, String checkOutDate) {
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    // JDBC-based method to add booking to database
    public void addBooking() {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO bookings (guestId, roomNumber, checkInDate, checkOutDate) " +
                         "VALUES (?, ?, ?, ?)";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, this.guestId);
            ps.setInt(2, Integer.parseInt(this.roomNumber));
            ps.setString(3, this.checkInDate);
            ps.setString(4, this.checkOutDate);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            
            // Update room availability to false (booked)
            updateRoomAvailability(false);
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }

    // Method to update room availability status
    private void updateRoomAvailability(boolean available) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE rooms SET availability = ? WHERE roomNumber = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, available);
            ps.setInt(2, Integer.parseInt(this.roomNumber));
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }

    // Static method to get booking by ID
    public static Booking getBookingById(int bookingId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM bookings WHERE id = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Booking(
                    rs.getInt("guestId"),
                    String.valueOf(rs.getInt("roomNumber")),
                    rs.getString("checkInDate"),
                    rs.getString("checkOutDate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        return null;
    }

    // Static method to delete booking
    public static boolean deleteBooking(int bookingId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First get the room number before deleting
            Booking booking = getBookingById(bookingId);
            if (booking == null) return false;
            
            // Delete the booking
            String sql = "DELETE FROM bookings WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update room availability to true (available)
                booking.updateRoomAvailability(true);
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseUtil.close(conn, ps, null);
        }
    }
}