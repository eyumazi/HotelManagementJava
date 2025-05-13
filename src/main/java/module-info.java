module pages.hotelmanagementjava {
    requires javafx.controls;
    requires javafx.fxml;
    opens pages.hotelmanagementjava.classes to javafx.base, javafx.fxml, javafx.controls;
    opens pages.hotelmanagementjava to javafx.fxml;
    exports pages.hotelmanagementjava;
}