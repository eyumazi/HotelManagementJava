package pages.hotelmanagementjava.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Guest extends Person {

    private int id;

    // Constructors
    public Guest(String firstName, String lastName, String phoneNumber, String email) {
        super(firstName, lastName, phoneNumber, email);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId (int id){this.id = id;}

    public void setId(){this.id = getNextId();}
    private int getNextId() {
        int nextId = 1; // Default value if file is empty
        try (BufferedReader reader = new BufferedReader(new FileReader("data/guestsinfo.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                int currentId = Integer.parseInt(parts[0]);
                if (currentId >= nextId) {
                    nextId = currentId + 1;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        updateFile(nextId);
        return nextId;
    }
    private void updateFile(int newId) {
        try (FileWriter writer = new FileWriter("data/guestsinfo.txt", true)) {
            // Append the new ID along with other information to the file
            writer.write(newId + "/" + getFirstName() + "/" + getLastName() + "/" + getPhoneNumber() + "/" + getEmail() + "\n");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception based on your needs
        }
    }

}
