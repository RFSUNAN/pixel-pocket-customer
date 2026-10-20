package PIXEL_POCKET.CUSTOMER;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Registration_Controller {

    // ─────────────────────────────────────────
    // PERSONAL DETAILS
    // ─────────────────────────────────────────
    @FXML private TextField   firstNameField;
    @FXML private TextField   lastNameField;
    @FXML private TextField   dobDayField;
    @FXML private TextField   dobMonthField;
    @FXML private TextField   dobYearField;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton otherRadio;
    @FXML private TextField   addressLine1Field;
    @FXML private TextField   addressLine2Field;
    @FXML private TextField   postcodeField;
    @FXML private TextField   townField;
    @FXML private TextField   countryField;
    @FXML private TextField   phoneField;

    // ─────────────────────────────────────────
    // ACCOUNT DETAILS
    // ─────────────────────────────────────────
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField     confirmVisible;

    // ─────────────────────────────────────────
    // LABELS
    // ─────────────────────────────────────────
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Label uploadedFileLabel;

    // ─────────────────────────────────────────
    // FILE UPLOAD STATE
    // ─────────────────────────────────────────
    private File uploadedIdFile;

    // ─────────────────────────────────────────
    // INITIALIZE — group radio buttons
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {
        ToggleGroup genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);
        otherRadio.setSelected(true);
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
    // TOGGLE CONFIRM PASSWORD EYE
    // ─────────────────────────────────────────
    @FXML
    public void toggleConfirmPassword() {
        if (confirmPasswordField.isVisible()) {
            confirmVisible.setText(confirmPasswordField.getText());
            confirmVisible.setVisible(true);
            confirmVisible.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            confirmPasswordField.setText(confirmVisible.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmVisible.setVisible(false);
            confirmVisible.setManaged(false);
        }
    }

    // ─────────────────────────────────────────
    // BROWSE FILE (click)
    // ─────────────────────────────────────────
    @FXML
    public void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ID Document");
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("ID Files (JPG, PNG, PDF)",
                "*.jpg", "*.jpeg", "*.png", "*.pdf")
        );
        File selected = fileChooser.showOpenDialog(
            emailField.getScene().getWindow()
        );
        setUploadedFile(selected);
    }

    // ─────────────────────────────────────────
    // DRAG OVER
    // ─────────────────────────────────────────
    @FXML
    public void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    // ─────────────────────────────────────────
    // DRAG DROPPED
    // ─────────────────────────────────────────
    @FXML
    public void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            setUploadedFile(db.getFiles().get(0));
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    // ─────────────────────────────────────────
    // VALIDATE AND STORE FILE
    // ─────────────────────────────────────────
    private void setUploadedFile(File file) {
        if (file == null) return;
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".jpg") && !name.endsWith(".jpeg")
                && !name.endsWith(".png")
                && !name.endsWith(".pdf")) {
            errorLabel.setText(
                "Invalid file type. Use JPG, PNG, or PDF.");
            return;
        }
        if (file.length() > 5L * 1024 * 1024) {
            errorLabel.setText("File exceeds the 5 MB limit.");
            return;
        }
        uploadedIdFile = file;
        uploadedFileLabel.setText("✔ " + file.getName());
        errorLabel.setText("");
    }

    // ─────────────────────────────────────────
    // HANDLE REGISTER BUTTON
    // ─────────────────────────────────────────
    @FXML
    public void handleRegister() {
        errorLabel.setText("");
        successLabel.setText("");

        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String dobDay    = dobDayField.getText().trim();
        String dobMonth  = dobMonthField.getText().trim();
        String dobYear   = dobYearField.getText().trim();
        String address1  = addressLine1Field.getText().trim();
        String postcode  = postcodeField.getText().trim();
        String town      = townField.getText().trim();
        String country   = countryField.getText().trim();
        String phone     = phoneField.getText().trim();
        String email     = emailField.getText().trim();

        String password = passwordField.isVisible()
            ? passwordField.getText().trim()
            : passwordVisible.getText().trim();
        String confirm = confirmPasswordField.isVisible()
            ? confirmPasswordField.getText().trim()
            : confirmVisible.getText().trim();

        // VALIDATION
        if (firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("Please enter your full name.");
            return;
        }
        if (email.isEmpty()) {
            errorLabel.setText("Please enter your email.");
            return;
        }
        if (dobDay.isEmpty() || dobMonth.isEmpty()
                || dobYear.isEmpty()) {
            errorLabel.setText(
                "Please enter your full date of birth.");
            return;
        }
        if (address1.isEmpty() || town.isEmpty()
                || postcode.isEmpty() || country.isEmpty()) {
            errorLabel.setText(
                "Please enter your full address.");
            return;
        }
        if (phone.isEmpty()) {
            errorLabel.setText(
                "Please enter your phone number.");
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Please enter a password.");
            return;
        }
        if (password.length() < 8 || password.length() > 30) {
            errorLabel.setText(
                "Password must be 8–30 characters.");
            return;
        }
        if (!password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")) {
            errorLabel.setText(
                "Password must contain upper and lowercase letters.");
            return;
        }
        if (!password.matches(".*[0-9].*")) {
            errorLabel.setText(
                "Password must contain at least one number.");
            return;
        }
        if (!password.matches(".*[!@#$%+].*")) {
            errorLabel.setText(
                "Password must contain a special character: !@#$%+");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match!");
            return;
        }

        String gender = "OTHER";
        if (maleRadio.isSelected())   gender = "MALE";
        if (femaleRadio.isSelected()) gender = "FEMALE";

        String dob = dobDay + "-" + dobMonth + "-" + dobYear;
        String idFilePath = (uploadedIdFile != null)
            ? uploadedIdFile.getAbsolutePath() : "";

        try {
            if (Customer_Controller.emailExists(email)) {
                errorLabel.setText("Email already registered!");
                return;
            }

            Customer_Controller.registerCustomer(
                firstName, lastName, dob, gender,
                address1, town, postcode, "", country,
                phone, email, password,
                idFilePath, "", "", ""
            );

            int id = Customer_Controller.loginCustomer(
                email, password);

            // ── UPDATED LINE ──────────────────
            PreferencesFxml_Controller.createPreferences(id);
            // ─────────────────────────────────

            successLabel.setText(
                "Account created! Redirecting to login...");

            javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(2));
            pause.setOnFinished(e -> goToLogin());
            pause.play();

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // GO TO LOGIN
    // ─────────────────────────────────────────
    @FXML
    public void goToLogin() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("Login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 750));
        } catch (IOException e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
}