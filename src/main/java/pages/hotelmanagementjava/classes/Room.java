package pages.hotelmanagementjava.classes;

public class Room {
    private int roomNumber;
    private String roomType;
    private String roomCapacity;
    private double price;
    private boolean availability;

    // Constructors
    public Room(int roomNumber, String roomType, String roomCapacity, double price, boolean availability) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomCapacity = roomCapacity;
        this.price = price;
        this.availability = availability;
    }

    // Getters and Setters
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(String roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    // Additional methods
    public void bookRoom() {
        if (availability) {
            availability = false;
            System.out.println("Room " + roomNumber + " booked successfully.");
        } else {
            System.out.println("Room " + roomNumber + " is not available for booking.");
        }
    }

    public void releaseRoom() {
        if (!availability) {
            availability = true;
            System.out.println("Room " + roomNumber + " released successfully.");
        } else {
            System.out.println("Room " + roomNumber + " is already available.");
        }
    }

    @Override
    public String toString() {
        return "Room Number: " + roomNumber +
                "\nRoom Type: " + roomType +
                "\nRoom Capacity: " + roomCapacity +
                "\nPrice: " + price +
                "\nAvailability: " + (availability ? "Available" : "Booked");
    }
}

