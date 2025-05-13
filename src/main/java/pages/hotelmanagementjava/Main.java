package pages.hotelmanagementjava;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene; //represents contents to be displayed on the window.
import javafx.stage.Stage; //represents the main window of the application. 
import java.io.IOException;//handles exceptions that may occur during the loading of the FXML file.

public class Main extends Application { //extends Application to use javaFX features
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setTitle("Hotel Management!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}