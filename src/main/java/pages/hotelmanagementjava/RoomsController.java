package pages.hotelmanagementjava;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pages.hotelmanagementjava.classes.Room;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomsController {
    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, Integer> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, String> roomCapacityColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, Boolean> availabilityColumn;


    @FXML
    private TextField roomNumberFilter;

    @FXML
    private RadioButton normalRadioBtn;

    @FXML
    private RadioButton economyRadioBtn;

    @FXML
    private RadioButton vipRadioBtn;

    @FXML
    private RadioButton singleRadioBtn;

    @FXML
    private RadioButton doubleRadioBtn;

    @FXML
    private RadioButton tripleRadioBtn;

    @FXML
    private RadioButton trueRadioBtn;

    @FXML
    private RadioButton falseRadioBtn;

    private ObservableList<Room> allRooms;

    @FXML
    private void initialize() {

        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("roomCapacity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Populate the table with data from the file
        allRooms = FXCollections.observableArrayList(readRoomsFromFile("data/room.txt"));
        roomsTable.setItems(allRooms);
    }

    private List<Room> readRoomsFromFile(String filePath) {
        List<Room> rooms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 5) {
                    int roomNumber = Integer.parseInt(parts[0]);
                    String roomType = parts[1];
                    String roomCapacity = parts[2];
                    double price = Double.parseDouble(parts[3]);
                    boolean availability = Boolean.parseBoolean(parts[4]);

                    Room room = new Room(roomNumber, roomType, roomCapacity, price, availability);
                    rooms.add(room);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
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


}
