package pages.hotelmanagementjava.classes;

public class Admin extends Person {
    private String username;
    private String password;

    // Constructors
    public Admin(String firstName, String lastName, String phoneNumber, String email,
                 String username, String password) {
        super(firstName, lastName, phoneNumber, email);
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
