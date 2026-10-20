package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountSettings_Controller {

    // ─────────────────────────────────────────
    // CHANGE PASSWORD
    // ─────────────────────────────────────────
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField     confirmVisible;
    @FXML private Label         passwordErrorLabel;
    @FXML private Label         passwordSuccessLabel;

    // ─────────────────────────────────────────
    // DEACTIVATE
    // ─────────────────────────────────────────
    @FXML private ComboBox<String> deactivateReasonCombo;
    @FXML private CheckBox         confirmDeactivateCheck;
    @FXML private Label            deactivateErrorLabel;

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {

        // POPULATE REASON DROPDOWN
        deactivateReasonCombo.getItems().addAll(
            "Account security concerns/Unauthorized activity",
            "I no longer use this service",
            "I have privacy concerns",
            "I want to create a new account",
            "Poor customer experience",
            "Too many emails/notifications",
            "Other"
        );
        deactivateReasonCombo.setValue(
            "Account security concerns/Unauthorized activity");
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
    // TOGGLE CONFIRM EYE
    // ─────────────────────────────────────────
    @FXML
    public void toggleConfirm() {
        if (confirmPasswordField.isVisible()) {
            confirmVisible.setText(
                confirmPasswordField.getText());
            confirmVisible.setVisible(true);
            confirmVisible.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            confirmPasswordField.setText(
                confirmVisible.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmVisible.setVisible(false);
            confirmVisible.setManaged(false);
        }
    }

    // ─────────────────────────────────────────
    // FORGOT PASSWORD
    // ─────────────────────────────────────────
    @FXML
    public void handleForgotPassword() {
        passwordErrorLabel.setStyle(
            "-fx-text-fill: #C3C8C9; -fx-font-size: 12;");
        passwordErrorLabel.setText(
            "Please contact support to reset your password.");
    }

    // ─────────────────────────────────────────
    // CHANGE PASSWORD
    // ─────────────────────────────────────────
    @FXML
    public void handleChangePassword() {
        passwordErrorLabel.setText("");
        passwordSuccessLabel.setText("");

        // GET VALUES
        String newPassword = passwordField.isVisible()
            ? passwordField.getText().trim()
            : passwordVisible.getText().trim();
        String confirm = confirmPasswordField.isVisible()
            ? confirmPasswordField.getText().trim()
            : confirmVisible.getText().trim();

        // VALIDATION
        if (newPassword.isEmpty()) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Please enter a new password.");
            return;
        }
        if (newPassword.length() < 8
                || newPassword.length() > 30) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Password must be 8–30 characters.");
            return;
        }
        if (!newPassword.matches(".*[A-Z].*")
                || !newPassword.matches(".*[a-z].*")) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Password must contain upper and lowercase letters.");
            return;
        }
        if (!newPassword.matches(".*[0-9].*")) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Password must contain at least one number.");
            return;
        }
        if (!newPassword.matches(".*[!@#$%+].*")) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Password must contain a special character: !@#$%+");
            return;
        }
        if (!newPassword.equals(confirm)) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Passwords do not match!");
            return;
        }

        // SAVE TO DB
        try {
            int customerId = Session.getCustomerId();
            updatePassword(customerId, newPassword);
            passwordSuccessLabel.setText(
                "✔ Password changed successfully!");

            // CLEAR FIELDS
            passwordField.clear();
            confirmPasswordField.clear();
            passwordVisible.clear();
            confirmVisible.clear();

        } catch (Exception e) {
            passwordErrorLabel.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
            passwordErrorLabel.setText(
                "Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // DEACTIVATE ACCOUNT
    // ─────────────────────────────────────────
    @FXML
    public void handleDeactivate() {
        deactivateErrorLabel.setText("");

        // MUST TICK CHECKBOX
        if (!confirmDeactivateCheck.isSelected()) {
            deactivateErrorLabel.setText(
                "You must tick the confirmation checkbox " +
                "to deactivate your account.");
            return;
        }

        int customerId = Session.getCustomerId();

        try {
            // CHECK FOR ACTIVE ORDERS FIRST
            boolean hasActive =
                Order_Controller.hasActiveOrders(customerId);

            if (hasActive) {
                deactivateErrorLabel.setText(
                    "Deactivation blocked: You have active orders. " +
                    "Wait until all orders are Delivered or " +
                    "Cancelled before deactivating.");
                return;
            }

            // DEACTIVATE
            Customer_Controller.deactivateCustomer(customerId);

            // CLEAR SESSION AND GO TO LOGIN
            Session.setCustomerId(0);
            Home_Controller.setSession(0, null);

            loadScene("Login.fxml", 900, 650);

        } catch (Exception e) {
            deactivateErrorLabel.setText(
                "Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // DB — UPDATE PASSWORD
    // ─────────────────────────────────────────
    private void updatePassword(int customerId, String newPassword)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL =
            "UPDATE CUSTOMERS SET password = ? " +
            "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, newPassword);
        ps.setInt(2, customerId);
        ps.executeUpdate();
        ps.close();
        c.close();

        System.out.println(
            "Password updated for: " + customerId);
    }

    // ─────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────
    @FXML
    public void goBack() {
        loadScene("Home.fxml", 1440, 1027);
    }

    @FXML
    public void goToHome() {
        loadScene("Home.fxml", 1440, 1027);
    }

    private void loadScene(String fxmlFile,
                           double width, double height) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(fxmlFile));
            Stage stage = (Stage)
                passwordField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            passwordErrorLabel.setText(
                "Error: " + e.getMessage());
        }
    }
}