package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Home_Controller {

    // ─────────────────────────────────────────
    // FXML FIELDS
    // ─────────────────────────────────────────
    @FXML private Label     usernameLabel;
    @FXML private ImageView logoImage;

    // ─────────────────────────────────────────
    // SESSION
    // ─────────────────────────────────────────
    private static int    loggedInCustomerId;
    private static String loggedInUsername;

    public static void setSession(int customerId, String username) {
        loggedInCustomerId = customerId;
        loggedInUsername   = username;
    }

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {

        // SET USERNAME
        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            usernameLabel.setText(loggedInUsername);
        }

        // LOAD LOGO IMAGE
        try {
            InputStream stream =
                getClass().getResourceAsStream("logo.png");
            if (stream != null) {
                logoImage.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // HOME BUTTON
    // ─────────────────────────────────────────
    @FXML
    public void goToHome() {
        // Already on home page
    }

    // ─────────────────────────────────────────
    // EDIT PROFILE
    // ─────────────────────────────────────────
    @FXML
    public void goToEditProfile() {
        loadScene("EditProfile.fxml", 1440, 1027);
    }

    // ─────────────────────────────────────────
    // ORDER HISTORY
    // ─────────────────────────────────────────
    @FXML
    public void goToOrderHistory() {
        loadScene("OrderHistory.fxml", 1440, 1027);
    }

    // ─────────────────────────────────────────
    // PREFERENCES
    // ─────────────────────────────────────────
    @FXML
    public void goToPreferences() {
        loadScene("Preferences.fxml", 1440, 1027);
    }

    // ─────────────────────────────────────────
    // ACCOUNT SETTINGS
    // ─────────────────────────────────────────
    @FXML
    public void goToAccountSettings() {
        loadScene("AccountSettings.fxml", 1440, 1027);
    }

    // ─────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────
    @FXML
    public void handleLogout() {
        loggedInCustomerId = 0;
        loggedInUsername   = null;
        loadScene("Login.fxml", 900, 650);
    }

    // ─────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────
    private void loadScene(String fxmlFile, double width, double height) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(fxmlFile));
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}