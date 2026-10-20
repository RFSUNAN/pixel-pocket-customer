package PIXEL_POCKET.CUSTOMER;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // LOAD LOGIN SCREEN
        Parent root = FXMLLoader.load(
            getClass().getResource(
                "/PIXEL_POCKET/CUSTOMER/Login.fxml"));

        Scene scene = new Scene(root, 1000, 750);
        stage.setTitle("Pixel-Pocket");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}