package pages.hotelmanagementjava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import pages.hotelmanagementjava.classes.Booking;
import pages.hotelmanagementjava.classes.Guest;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CheckinController {

    @FXML
    private ToggleGroup capacity;

    @FXML
    private DatePicker checkindate;

    @FXML
    private DatePicker checkoutdate;

    @FXML
    private RadioButton doubleRb;

    @FXML
    private RadioButton economyRb;

    @FXML
    private TextField email;

    @FXML
    private Label error;

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    @FXML
    private RadioButton normalRb;

    @FXML
    private TextField phonenumber;

    @FXML
    private RadioButton singleRb;

    @FXML
    private RadioButton tripleRb;

    @FXML
    private ToggleGroup type;

    @FXML
    private RadioButton vipRb;

    @FXML
    private Label price;

    @FXML
    private Label roomNumber;

    boolean found = false;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Function to validate customer data
    private boolean validateCustomerData() {
        if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || phonenumber.getText().isEmpty()
                || email.getText().isEmpty()) {
            showError("All fields must be filled.");
            return false;
        }

        // Validate email format
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (!email.getText().matches(emailPattern)) {
            showError("Please enter a valid email address.");
            return false;
        }

        return true;
    }


    private boolean validateRoomAndDates() {
        if (type.getSelectedToggle() == null || capacity.getSelectedToggle() == null
                || checkindate.getValue() == null || checkoutdate.getValue() == null) {
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

        // Validate input fields
        if (!validateCustomerData() || !validateRoomAndDates()) {
            return;
        }

        String roomType = getSelectedRadioButton(type);
        String roomCapacity = getSelectedRadioButton(capacity);

        String filePath = "data/room.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 5 && parts[1].equalsIgnoreCase(roomType) && parts[2].equalsIgnoreCase(roomCapacity)
                        && parts[4].equalsIgnoreCase("true")) {
                    roomNumber.setText(parts[0]);
                    try {
                        double Price = Double.parseDouble(parts[3]);
                        double totalPrice = calculatePrice(Price, checkindate.getValue().toString(), checkoutdate.getValue().toString());
                        String total = String.valueOf(totalPrice);
                        price.setText(total);
                        found = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid double format");
                    }

                    return;
                }
            }
            showError("No available room matching your criteria.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error reading room information. Please try again.");
        }
    }

    private double calculatePrice(double price, String checkinDate, String checkoutDate) {
        LocalDate startDate = LocalDate.parse(checkinDate, dateFormatter);
        LocalDate endDate = LocalDate.parse(checkoutDate, dateFormatter);

        int days = (int) startDate.until(endDate).getDays();

        return price * days;
    }

    private String getSelectedRadioButton(ToggleGroup toggleGroup) {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        return selectedRadioButton.getText();
    }

    @FXML
    private void confirm() {
        if (validateCustomerData() && validateRoomAndDates() && found) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Check-in has been successful!");
            alert.setContentText("Thank you for choosing our hotel.");
            alert.showAndWait();
            String firstName = firstname.getText();
            String lastName = lastname.getText();
            String phoneNumber = phonenumber.getText();
            String emailAddress = email.getText();
            String roomnumber = roomNumber.getText();
            String checkin = checkindate.getValue().toString();
            String checkout = checkoutdate.getValue().toString();
            Guest guest = new Guest(firstName, lastName, phoneNumber, emailAddress);
            guest.setId();
            Booking book = new Booking(guest.getId(), roomnumber, checkin, checkout);
            book.addBooking();
            List<String> lines = readRoomFile("data/room.txt");
            updateAvailability(lines, Integer.parseInt(roomNumber.getText()), false);
            writeRoomFile("data/room.txt", lines);
            clear();
        } else {
            showError("Confirmation Unsuccessful!");
        }
    }

    @FXML
    private void clear() {
        clearError();

        // Clear text fields
        firstname.clear();
        lastname.clear();
        phonenumber.clear();
        email.clear();

        // Clear radio buttons
        type.selectToggle(null);
        capacity.selectToggle(null);

        // Clear date pickers
        checkindate.getEditor().clear();
        checkoutdate.getEditor().clear();

        // Clear labels
        roomNumber.setText("");
        price.setText("");
    }

    public static List<String> readRoomFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static void updateAvailability(List<String> lines, int roomNumber, boolean newAvailability) {
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("/");
            if (parts.length == 5) {
                int currentRoomNumber = Integer.parseInt(parts[0]);
                if (currentRoomNumber == roomNumber) {
                    parts[4] = String.valueOf(newAvailability);
                    lines.set(i, String.join("/", parts));
                    break;
                }
            }
        }
    }

    public static void writeRoomFile(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
