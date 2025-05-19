package pages.hotelmanagementjava.classes;

import pages.hotelmanagementjava.DatabaseUtil;
import java.sql.*;

public class Guest extends Person {
    private int id;

    // Constructors
    public Guest(String firstName, String lastName, String phoneNumber, String email) {
        super(firstName, lastName, phoneNumber, email);
    }

    public Guest(int id, String firstName, String lastName, String phoneNumber, String email) {
        super(firstName, lastName, phoneNumber, email);
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Automatically sets the next available ID from database
    public void setId() {
        this.id = getNextId();
    }

    // Gets the next available ID from database
    private int getNextId() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM guests");
            
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
            return 1; // If table is empty
        } catch (SQLException e) {
            e.printStackTrace();
            return 1; // Fallback value
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }

    // Saves guest to database
    public void saveGuest() {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO guests (id, firstName, lastName, phone, email) " +
                         "VALUES (?, ?, ?, ?, ?)";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ps.setString(2, this.getFirstName());
            ps.setString(3, this.getLastName());
            ps.setString(4, this.getPhoneNumber());
            ps.setString(5, this.getEmail());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }

    // Static method to get guest by ID
    public static Guest getGuestById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM guests WHERE id = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Guest(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        return null;
    }

    // Static method to delete guest
    public static boolean deleteGuest(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM guests WHERE id = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }

    // Static method to update guest
    public static boolean updateGuest(Guest guest) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE guests SET firstName = ?, lastName = ?, phone = ?, email = ? " +
                         "WHERE id = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, guest.getFirstName());
            ps.setString(2, guest.getLastName());
            ps.setString(3, guest.getPhoneNumber());
            ps.setString(4, guest.getEmail());
            ps.setInt(5, guest.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.close(conn, ps, null);
        }
    }
}