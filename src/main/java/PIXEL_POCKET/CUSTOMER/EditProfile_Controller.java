package PIXEL_POCKET.CUSTOMER;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditProfile_Controller {

    // ─────────────────────────────────────────
    // PERSONAL DETAILS
    // ─────────────────────────────────────────
    @FXML private TextField        firstNameField;
    @FXML private TextField        lastNameField;
    @FXML private TextField        dobDayField;
    @FXML private ComboBox<String> dobMonthCombo;
    @FXML private TextField        dobYearField;
    @FXML private RadioButton      maleRadio;
    @FXML private RadioButton      femaleRadio;
    @FXML private RadioButton      otherRadio;
    @FXML private TextField        addressLine1Field;
    @FXML private TextField        addressLine2Field;
    @FXML private TextField        postcodeField;
    @FXML private TextField        townField;
    @FXML private ComboBox<String> countryCombo;
    @FXML private ComboBox<String> dialCodeCombo;
    @FXML private TextField        phoneField;

    // ─────────────────────────────────────────
    // ACCOUNT — READ ONLY
    // ─────────────────────────────────────────
    @FXML private TextField emailField;
    @FXML private TextField userIdField;

    // ─────────────────────────────────────────
    // PAYMENT
    // ─────────────────────────────────────────
    @FXML private TextField cardNumberField;
    @FXML private TextField sortCodeField;
    @FXML private TextField cvvField;

    // ─────────────────────────────────────────
    // LABELS
    // ─────────────────────────────────────────
    @FXML private Label idFileLabel;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private File   uploadedIdFile;
    private String existingIdPath = "";

    // ─────────────────────────────────────────
    // SHARED FIELD STYLE
    // ─────────────────────────────────────────
    private static final String FIELD_STYLE =
        "-fx-background-color: rgba(195,200,201,0.9);" +
        "-fx-background-radius: 26;" +
        "-fx-font-size: 15;" +
        "-fx-text-fill: #1a1a1a;" +
        "-fx-prompt-text-fill: #3a5a5f;" +
        "-fx-control-inner-background: rgba(195,200,201,0.9);";

    private static final String FIELD_STYLE_SM =
        "-fx-background-color: rgba(195,200,201,0.9);" +
        "-fx-background-radius: 26;" +
        "-fx-font-size: 13;" +
        "-fx-text-fill: #1a1a1a;" +
        "-fx-prompt-text-fill: #3a5a5f;" +
        "-fx-control-inner-background: rgba(195,200,201,0.9);";

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {

        // GENDER GROUP
        ToggleGroup genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);

        // MONTH DROPDOWN
        dobMonthCombo.getItems().addAll(
            "January","February","March","April",
            "May","June","July","August",
            "September","October","November","December"
        );

        // DAY — numbers only, max 2 digits
        dobDayField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d{0,2}")) dobDayField.setText(old);
        });

        // YEAR — numbers only, max 4 digits
        dobYearField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d{0,4}")) dobYearField.setText(old);
        });

        // PHONE — numbers only
        phoneField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*")) phoneField.setText(old);
        });

        // DIAL CODE DROPDOWN
        dialCodeCombo.getItems().addAll(
            "🇬🇧 +44",
            "🇺🇸 +1",
            "🇨🇦 +1",
            "🇦🇺 +61",
            "🇮🇪 +353",
            "🇫🇷 +33",
            "🇩🇪 +49",
            "🇪🇸 +34",
            "🇮🇹 +39",
            "🇧🇩 +880",
            "🇮🇳 +91",
            "🇵🇰 +92",
            "🇳🇬 +234",
            "🇿🇦 +27",
            "🇧🇷 +55",
            "🇯🇵 +81",
            "🇨🇳 +86",
            "🇸🇦 +966"
        );
        dialCodeCombo.setValue("🇬🇧 +44");

        // COUNTRY DROPDOWN
        countryCombo.getItems().addAll(
            "United Kingdom","United States","Canada",
            "Australia","Ireland","France","Germany",
            "Spain","Italy","Bangladesh","India",
            "Pakistan","Nigeria","South Africa",
            "Brazil","Japan","China","Saudi Arabia","Other"
        );

        // LOAD DATA FROM DB
        int customerId = Session.getCustomerId();
        try {
            String[] data =
                Customer_Controller.getCustomerById(customerId);

            if (data != null) {
                firstNameField.setText(data[0] != null ? data[0] : "");
                lastNameField.setText(data[1]  != null ? data[1] : "");

                // DOB split "01-January-2000"
                if (data[2] != null && data[2].contains("-")) {
                    String[] dob = data[2].split("-");
                    if (dob.length == 3) {
                        dobDayField.setText(dob[0]);
                        dobMonthCombo.setValue(dob[1]);
                        dobYearField.setText(dob[2]);
                    }
                }

                // GENDER
                if (data[3] != null) {
                    switch (data[3].toUpperCase()) {
                        case "MALE"   -> maleRadio.setSelected(true);
                        case "FEMALE" -> femaleRadio.setSelected(true);
                        default       -> otherRadio.setSelected(true);
                    }
                }

                addressLine1Field.setText(data[4] != null ? data[4] : "");
                addressLine2Field.setText(data[5] != null ? data[5] : "");
                postcodeField.setText(data[6]     != null ? data[6] : "");
                townField.setText(data[7]         != null ? data[7] : "");
                countryCombo.setValue(data[8]     != null ? data[8] : "");
                phoneField.setText(data[9]        != null ? data[9] : "");
                emailField.setText(data[10]       != null ? data[10] : "");
                userIdField.setText(data[11]      != null ? data[11] : "");
                cardNumberField.setText(data[12]  != null ? data[12] : "");
                sortCodeField.setText(data[13]    != null ? data[13] : "");
                cvvField.setText(data[14]         != null ? data[14] : "");
            }

        } catch (Exception e) {
            errorLabel.setText("Could not load profile: "
                               + e.getMessage());
        }

        // FORCE TEXT COLOUR AFTER DATA LOADS
        Platform.runLater(() -> {
            firstNameField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            lastNameField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            dobDayField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            dobYearField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            addressLine1Field.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            addressLine2Field.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            postcodeField.setStyle(
                FIELD_STYLE_SM + "-fx-padding: 0 10;");
            townField.setStyle(
                FIELD_STYLE_SM + "-fx-padding: 0 10;");
            phoneField.setStyle(
                FIELD_STYLE_SM + "-fx-padding: 0 10;");
            cardNumberField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            sortCodeField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
            cvvField.setStyle(
                FIELD_STYLE + "-fx-padding: 0 14;");
        });
    }

    // ─────────────────────────────────────────
    // ID CARD UPLOAD
    // ─────────────────────────────────────────
    @FXML
    public void handleIdUpload() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select ID Document");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "ID Files", "*.jpg", "*.jpeg", "*.png", "*.pdf")
        );
        File selected = fc.showOpenDialog(
            errorLabel.getScene().getWindow());

        if (selected != null) {
            if (selected.length() > 5L * 1024 * 1024) {
                errorLabel.setText("File exceeds 5 MB limit.");
                return;
            }
            uploadedIdFile = selected;
            idFileLabel.setText("✔ " + selected.getName());
            errorLabel.setText("");
        }
    }

    // ─────────────────────────────────────────
    // SAVE CHANGES
    // ─────────────────────────────────────────
    @FXML
    public void handleSave() {
        errorLabel.setText("");
        successLabel.setText("");

        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String dobDay    = dobDayField.getText().trim();
        String dobMonth  = dobMonthCombo.getValue() != null
                           ? dobMonthCombo.getValue() : "";
        String dobYear   = dobYearField.getText().trim();
        String address1  = addressLine1Field.getText().trim();
        String address2  = addressLine2Field.getText().trim();
        String postcode  = postcodeField.getText().trim();
        String town      = townField.getText().trim();
        String country   = countryCombo.getValue() != null
                           ? countryCombo.getValue() : "";
        String phone     = phoneField.getText().trim();
        String email     = emailField.getText().trim();
        String cardNum   = cardNumberField.getText().trim();
        String sortCode  = sortCodeField.getText().trim();
        String cvv       = cvvField.getText().trim();

        // VALIDATION
        if (firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("Please enter your full name.");
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

        // GENDER
        String gender = "OTHER";
        if (maleRadio.isSelected())   gender = "MALE";
        if (femaleRadio.isSelected()) gender = "FEMALE";

        String dob    = dobDay + "-" + dobMonth + "-" + dobYear;
        String idPath = uploadedIdFile != null
                        ? uploadedIdFile.getAbsolutePath()
                        : existingIdPath;

        try {
            int customerId = Session.getCustomerId();
            Customer_Controller.updateCustomer(
                customerId,
                firstName, lastName, dob, gender,
                address1, address2,
                postcode, town, country,
                phone, email,
                cardNum, sortCode, cvv,
                idPath
            );
            successLabel.setText("✔ Profile updated successfully!");

        } catch (Exception e) {
            errorLabel.setText("Error saving: " + e.getMessage());
        }
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

    // ─────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────
    private void loadScene(String fxmlFile,
                           double width, double height) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(fxmlFile));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
}