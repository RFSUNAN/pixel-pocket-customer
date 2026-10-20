package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLogonFxml_Controller {

    // ─────────────────────────────────────────
    // FXML FIELDS
    // ─────────────────────────────────────────
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private Label         errorLabel;

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
    // HANDLE LOGIN
    // ─────────────────────────────────────────
    @FXML
    public void handleLogin() {
        errorLabel.setStyle(
            "-fx-text-fill: #ff6b6b; -fx-font-size: 13;");
        errorLabel.setText("");

        String email = emailField.getText().trim();
        String password = passwordField.isVisible()
            ? passwordField.getText().trim()
            : passwordVisible.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText(
                "Please enter email and password.");
            return;
        }

        try {
            int adminId = loginAdmin(email, password);

            if (adminId == -1) {
                errorLabel.setText(
                    "Invalid admin email or password.");
            } else {
                // SUCCESS — load admin home page
                Parent root = FXMLLoader.load(
                    getClass().getResource("AdminHome.fxml"));
                Stage stage = (Stage)
                    emailField.getScene().getWindow();
                stage.setScene(new Scene(root, 1440, 1027));
            }

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            System.out.println(
                "Admin login error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // FORGOT PASSWORD
    // ─────────────────────────────────────────
    @FXML
    public void handleForgotPassword() {
        errorLabel.setStyle(
            "-fx-text-fill: #C3C8C9; -fx-font-size: 13;");
        errorLabel.setText(
            "Please contact your system administrator.");
    }

    // ─────────────────────────────────────────
    // GO TO REGISTER
    // ─────────────────────────────────────────
    @FXML
    public void goToRegister() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("Registration.fxml"));
            Stage stage = (Stage)
                emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 900));
        } catch (IOException e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // DB — LOGIN ADMIN
    // Returns admin_id if correct, -1 if wrong
    // ─────────────────────────────────────────
    private int loginAdmin(String email, String password)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL =
            "SELECT admin_id FROM ADMINS " +
            "WHERE email = ? AND password = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("admin_id");
            rs.close(); ps.close(); c.close();
            System.out.println(
                "Admin Login Successful! ID: " + id);
            return id;
        }

        rs.close(); ps.close(); c.close();
        System.out.println("Admin Login Failed!");
        return -1;
    }
}