package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login_Controller {

    // ─────────────────────────────────────────
    // FXML ELEMENTS
    // ─────────────────────────────────────────
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private Button        eyeBtn;
    @FXML private Label         errorLabel;

    // ─────────────────────────────────────────
    // HANDLE LOGIN
    // ─────────────────────────────────────────
    @FXML
    public void handleLogin() {

        String email = emailField.getText().trim();

        String password = passwordField.isVisible()
            ? passwordField.getText().trim()
            : passwordVisible.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        try {
            int id = Customer_Controller.loginCustomer(email, password);

            if (id == -1) {
                errorLabel.setText("Invalid email or password.");
            } else {
                // SET SESSION BEFORE LOADING HOME
                Session.setCustomerId(id);
                Home_Controller.setSession(id, email);

                // LOAD HOME PAGE
                Parent root = FXMLLoader.load(
                    getClass().getResource("Home.fxml"));
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root, 1440, 1027));
            }

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            System.out.println("Login error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // TOGGLE PASSWORD EYE
    // ─────────────────────────────────────────
    @FXML
    public void togglePassword() {
        if (passwordField.isVisible()) {
            passwordVisible.setText(passwordField.getText());
            passwordVisible.setVisible(true);
            passwordVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordVisible.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisible.setVisible(false);
            passwordVisible.setManaged(false);
        }
    }

    // ─────────────────────────────────────────
    // GO TO REGISTER
    // ─────────────────────────────────────────
    @FXML
    public void goToRegister() {
        goToScreen("Registration.fxml", 1200, 900);
    }

    // ─────────────────────────────────────────
    // GO TO ADMIN LOGIN
    // ─────────────────────────────────────────
    @FXML
    public void goToAdminLogin() {
        goToScreen("AdminLogin.fxml", 900, 650);
    }

    // ─────────────────────────────────────────
    // FORGOT PASSWORD
    // ─────────────────────────────────────────
    @FXML
    public void handleForgotPassword() {
        errorLabel.setText("Please contact support to reset your password.");
    }

    // ─────────────────────────────────────────
    // SOCIAL LOGIN BUTTONS
    // ─────────────────────────────────────────
    @FXML
    public void handleFacebookLogin() {
        errorLabel.setText("Facebook login coming soon.");
    }

    @FXML
    public void handleInstagramLogin() {
        errorLabel.setText("Instagram login coming soon.");
    }

    @FXML
    public void handleAppleLogin() {
        errorLabel.setText("Apple login coming soon.");
    }

    // ─────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────
    private void goToScreen(String fxmlFile, double width, double height) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(fxmlFile));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            errorLabel.setText("Error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }
}