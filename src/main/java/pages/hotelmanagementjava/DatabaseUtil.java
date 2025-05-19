package pages.hotelmanagementjava;
import java.sql.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASSWORD = "2025Group1#"; // Change to your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }

    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
                       
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "firstName VARCHAR(100), lastName VARCHAR(100), username VARCHAR(100) UNIQUE, password VARCHAR(100), phone VARCHAR(100), email VARCHAR(100))");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS guests (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "firstName VARCHAR(100), lastName VARCHAR(100), phone VARCHAR(100), email VARCHAR(100))");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (" +
                    "roomNumber INTEGER PRIMARY KEY," +
                    "roomType VARCHAR(100), roomCapacity VARCHAR(100), price REAL, availability BOOLEAN)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "guestId INTEGER, roomNumber INTEGER, checkInDate VARCHAR(100), checkOutDate VARCHAR(100)," +
                    "FOREIGN KEY(guestId) REFERENCES guests(id)," +
                    "FOREIGN KEY(roomNumber) REFERENCES rooms(roomNumber))");
            
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt, null);
        }
    }

    public static void main(String[] args) {
    }
}