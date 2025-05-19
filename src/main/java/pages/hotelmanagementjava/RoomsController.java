package pages.hotelmanagementjava;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pages.hotelmanagementjava.classes.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomsController {
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Integer> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, String> roomCapacityColumn;
    @FXML private TableColumn<Room, Double> priceColumn;
    @FXML private TableColumn<Room, Boolean> availabilityColumn;
    
    @FXML private TextField roomNumberFilter;
    @FXML private RadioButton normalRadioBtn;
    @FXML private RadioButton economyRadioBtn;
    @FXML private RadioButton vipRadioBtn;
    @FXML private RadioButton singleRadioBtn;
    @FXML private RadioButton doubleRadioBtn;
    @FXML private RadioButton tripleRadioBtn;
    @FXML private RadioButton trueRadioBtn;
    @FXML private RadioButton falseRadioBtn;

    private ObservableList<Room> allRooms;

    @FXML
    private void initialize() {
        // Initialize table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("roomCapacity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Load data from database
        allRooms = FXCollections.observableArrayList(loadRoomsFromDatabase());
        roomsTable.setItems(allRooms);
    }

    private List<Room> loadRoomsFromDatabase() {
        List<Room> rooms = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM rooms");

            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("roomNumber"),
                    rs.getString("roomType"),
                    rs.getString("roomCapacity"),
                    rs.getDouble("price"),
                    rs.getBoolean("availability")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to load rooms");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }

        return rooms;
    }

    @FXML
    private void filterRooms() {
        List<Room> filteredRooms = allRooms.stream()
                .filter(this::matchesFilterCriteria)
                .collect(Collectors.toList());

        roomsTable.setItems(FXCollections.observableArrayList(filteredRooms));
    }

    private boolean matchesFilterCriteria(Room room) {
        boolean matchesRoomNumber = roomNumberFilter.getText().isEmpty() ||
                Integer.toString(room.getRoomNumber()).equals(roomNumberFilter.getText());

        boolean matchesRoomType = !isRoomTypeSelected() ||
                (normalRadioBtn.isSelected() && room.getRoomType().equalsIgnoreCase("normal")) ||
                (economyRadioBtn.isSelected() && room.getRoomType().equalsIgnoreCase("economy")) ||
                (vipRadioBtn.isSelected() && room.getRoomType().equalsIgnoreCase("vip"));

        boolean matchesRoomCapacity = !isRoomCapacitySelected() ||
                (singleRadioBtn.isSelected() && room.getRoomCapacity().equalsIgnoreCase("single")) ||
                (doubleRadioBtn.isSelected() && room.getRoomCapacity().equalsIgnoreCase("double")) ||
                (tripleRadioBtn.isSelected() && room.getRoomCapacity().equalsIgnoreCase("triple"));

        boolean matchesAvailability = !isAvailabilitySelected() ||
                (trueRadioBtn.isSelected() && room.isAvailability()) ||
                (falseRadioBtn.isSelected() && !room.isAvailability());

        return matchesRoomNumber && matchesRoomType && matchesRoomCapacity && matchesAvailability;
    }

    private boolean isRoomTypeSelected() {
        return normalRadioBtn.isSelected() || economyRadioBtn.isSelected() || vipRadioBtn.isSelected();
    }

    private boolean isRoomCapacitySelected() {
        return singleRadioBtn.isSelected() || doubleRadioBtn.isSelected() || tripleRadioBtn.isSelected();
    }

    private boolean isAvailabilitySelected() {
        return trueRadioBtn.isSelected() || falseRadioBtn.isSelected();
    }

    @FXML
    private void clearFilters() {
        roomNumberFilter.clear();
        normalRadioBtn.setSelected(false);
        economyRadioBtn.setSelected(false);
        vipRadioBtn.setSelected(false);
        singleRadioBtn.setSelected(false);
        doubleRadioBtn.setSelected(false);
        tripleRadioBtn.setSelected(false);
        trueRadioBtn.setSelected(false);
        falseRadioBtn.setSelected(false);

        roomsTable.setItems(allRooms);
    }

    // Additional method to refresh data from database
    @FXML
    private void refreshData() {
        allRooms = FXCollections.observableArrayList(loadRoomsFromDatabase());
        roomsTable.setItems(allRooms);
    }
}