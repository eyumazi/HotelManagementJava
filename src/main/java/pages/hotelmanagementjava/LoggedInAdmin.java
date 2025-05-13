package pages.hotelmanagementjava;public class LoggedInAdmin {

    private static LoggedInAdmin instance;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String adminEmail;
    private String adminPhone;

    private LoggedInAdmin() {
        // Private constructor to prevent instantiation.
    }

    public static LoggedInAdmin getInstance() {
        if (instance == null) {
            instance = new LoggedInAdmin();
        }
        return instance;
    }

    // Getters and setters for admin information...

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
}
