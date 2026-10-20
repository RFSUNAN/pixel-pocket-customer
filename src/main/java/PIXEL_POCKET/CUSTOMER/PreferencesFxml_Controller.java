package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class PreferencesFxml_Controller {

    // ─────────────────────────────────────────
    // NOTIFICATION TOGGLES
    // ─────────────────────────────────────────
    @FXML private ToggleButton promoEmailToggle;
    @FXML private ToggleButton smsAlertToggle;
    @FXML private ToggleButton wishlistToggle;

    // ─────────────────────────────────────────
    // CATEGORY TOGGLES
    // ─────────────────────────────────────────
    @FXML private ToggleButton clothesBtn;
    @FXML private ToggleButton sneakersBtn;
    @FXML private ToggleButton electronicsBtn;
    @FXML private ToggleButton homeBtn;
    @FXML private ToggleButton beautyBtn;
    @FXML private ToggleButton jewelryBtn;
    @FXML private ToggleButton healthBtn;
    @FXML private ToggleButton toysBtn;
    @FXML private ToggleButton petBtn;

    // ─────────────────────────────────────────
    // DELIVERY + LABELS
    // ─────────────────────────────────────────
    @FXML private TextArea deliveryInstructions;
    @FXML private Label    charCountLabel;
    @FXML private Label    errorLabel;
    @FXML private Label    successLabel;

    // ─────────────────────────────────────────
    // NOTIFICATION TOGGLE STYLES
    // ─────────────────────────────────────────
    private static final String TOGGLE_ON =
        "-fx-background-color: #4CAF50;" +
        "-fx-background-radius: 14;" +
        "-fx-text-fill: transparent;" +
        "-fx-cursor: hand;" +
        "-fx-padding: 0;";

    private static final String TOGGLE_OFF =
        "-fx-background-color: #888888;" +
        "-fx-background-radius: 14;" +
        "-fx-text-fill: transparent;" +
        "-fx-cursor: hand;" +
        "-fx-padding: 0;";

    // ─────────────────────────────────────────
    // CATEGORY BUTTON STYLES
    // ─────────────────────────────────────────
    private static final String ON_STYLE =
        "-fx-background-color: #1D9C9C;" +
        "-fx-background-radius: 26;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 15;" +
        "-fx-padding: 10 24;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: #1D9C9C;" +
        "-fx-border-radius: 26;" +
        "-fx-border-width: 1;";

    private static final String OFF_STYLE =
        "-fx-background-color: rgba(195,200,201,0.25);" +
        "-fx-background-radius: 26;" +
        "-fx-text-fill: #C3C8C9;" +
        "-fx-font-size: 15;" +
        "-fx-padding: 10 24;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: rgba(195,200,201,0.4);" +
        "-fx-border-radius: 26;" +
        "-fx-border-width: 1;";

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {

        // CHARACTER COUNTER
        deliveryInstructions.textProperty()
            .addListener((obs, old, val) -> {
                if (val.length() > 250) {
                    deliveryInstructions.setText(
                        val.substring(0, 250));
                    return;
                }
                charCountLabel.setText(val.length() + "/250");
            });

        // LOAD FROM DB
        int customerId = Session.getCustomerId();
        try {
            String[] prefs = getPreferences(customerId);

            if (prefs != null) {
                setNotifToggle(promoEmailToggle,
                    "1".equals(prefs[0]));
                setNotifToggle(smsAlertToggle,
                    "1".equals(prefs[1]));
                setNotifToggle(wishlistToggle,
                    "1".equals(prefs[2]));

                if (prefs[3] != null) {
                    deliveryInstructions.setText(prefs[3]);
                }

                if (prefs[4] != null) {
                    String cats = prefs[4];
                    setCatToggle(clothesBtn,
                        cats.contains("Clothes"));
                    setCatToggle(sneakersBtn,
                        cats.contains("Sneakers"));
                    setCatToggle(electronicsBtn,
                        cats.contains("Electronics"));
                    setCatToggle(homeBtn,
                        cats.contains("Home & Accessories"));
                    setCatToggle(beautyBtn,
                        cats.contains("Beauty & Fragrance"));
                    setCatToggle(jewelryBtn,
                        cats.contains("Jewelry & Watches"));
                    setCatToggle(healthBtn,
                        cats.contains("Health & Wellness"));
                    setCatToggle(toysBtn,
                        cats.contains("Toys & Games"));
                    setCatToggle(petBtn,
                        cats.contains("Pet Supplies"));
                }
            } else {
                // NO ROW YET — set all defaults ON
                setNotifToggle(promoEmailToggle, true);
                setNotifToggle(smsAlertToggle,   true);
                setNotifToggle(wishlistToggle,   true);
            }

        } catch (Exception e) {
            errorLabel.setText(
                "Could not load preferences: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFICATION TOGGLE HANDLER
    // ─────────────────────────────────────────
    @FXML
    public void handleToggle() {
        updateNotifStyle(promoEmailToggle);
        updateNotifStyle(smsAlertToggle);
        updateNotifStyle(wishlistToggle);
    }

    // ─────────────────────────────────────────
    // CATEGORY HANDLER
    // ─────────────────────────────────────────
    @FXML
    public void handleCategory() {
        updateCatStyle(clothesBtn);
        updateCatStyle(sneakersBtn);
        updateCatStyle(electronicsBtn);
        updateCatStyle(homeBtn);
        updateCatStyle(beautyBtn);
        updateCatStyle(jewelryBtn);
        updateCatStyle(healthBtn);
        updateCatStyle(toysBtn);
        updateCatStyle(petBtn);
    }

    // ─────────────────────────────────────────
    // SAVE
    // ─────────────────────────────────────────
    @FXML
    public void handleSave() {
        errorLabel.setText("");
        successLabel.setText("");

        int customerId = Session.getCustomerId();

        int promoEmail = promoEmailToggle.isSelected() ? 1 : 0;
        int smsAlert   = smsAlertToggle.isSelected()   ? 1 : 0;
        int wishlist   = wishlistToggle.isSelected()   ? 1 : 0;

        String delivery =
            deliveryInstructions.getText().trim();

        List<String> selected = new ArrayList<>();
        if (clothesBtn.isSelected())
            selected.add("Clothes");
        if (sneakersBtn.isSelected())
            selected.add("Sneakers");
        if (electronicsBtn.isSelected())
            selected.add("Electronics");
        if (homeBtn.isSelected())
            selected.add("Home & Accessories");
        if (beautyBtn.isSelected())
            selected.add("Beauty & Fragrance");
        if (jewelryBtn.isSelected())
            selected.add("Jewelry & Watches");
        if (healthBtn.isSelected())
            selected.add("Health & Wellness");
        if (toysBtn.isSelected())
            selected.add("Toys & Games");
        if (petBtn.isSelected())
            selected.add("Pet Supplies");

        String categories = String.join(",", selected);

        try {
            savePreferences(
                customerId,
                promoEmail, smsAlert, wishlist,
                delivery, categories
            );
            successLabel.setText("✔ Preferences saved!");

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // DB — GET PREFERENCES
    // ─────────────────────────────────────────
    public static String[] getPreferences(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL =
            "SELECT promo_email, sms_alert, wishlist_sale, " +
            "delivery_instructions, preferred_categories " +
            "FROM PREFERENCES WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String[] data = new String[5];
            data[0] = rs.getString("promo_email");
            data[1] = rs.getString("sms_alert");
            data[2] = rs.getString("wishlist_sale");
            data[3] = rs.getString("delivery_instructions");
            data[4] = rs.getString("preferred_categories");
            rs.close(); ps.close(); c.close();
            return data;
        }

        rs.close(); ps.close(); c.close();
        return null;
    }

    // ─────────────────────────────────────────
    // DB — SAVE PREFERENCES
    // ─────────────────────────────────────────
    public static void savePreferences(
            int customerId,
            int promoEmail, int smsAlert, int wishlistSale,
            String delivery, String categories)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        PreparedStatement check = c.prepareStatement(
            "SELECT COUNT(*) FROM PREFERENCES " +
            "WHERE customer_id = ?"
        );
        check.setInt(1, customerId);
        ResultSet rs = check.executeQuery();
        int count = rs.getInt(1);
        rs.close(); check.close();

        if (count > 0) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE PREFERENCES SET " +
                "promo_email = ?, sms_alert = ?, " +
                "wishlist_sale = ?, " +
                "delivery_instructions = ?, " +
                "preferred_categories = ? " +
                "WHERE customer_id = ?"
            );
            ps.setInt(1,    promoEmail);
            ps.setInt(2,    smsAlert);
            ps.setInt(3,    wishlistSale);
            ps.setString(4, delivery);
            ps.setString(5, categories);
            ps.setInt(6,    customerId);
            ps.executeUpdate();
            ps.close();
        } else {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO PREFERENCES " +
                "(customer_id, promo_email, sms_alert, " +
                "wishlist_sale, delivery_instructions, " +
                "preferred_categories) " +
                "VALUES (?,?,?,?,?,?)"
            );
            ps.setInt(1,    customerId);
            ps.setInt(2,    promoEmail);
            ps.setInt(3,    smsAlert);
            ps.setInt(4,    wishlistSale);
            ps.setString(5, delivery);
            ps.setString(6, categories);
            ps.executeUpdate();
            ps.close();
        }

        c.close();
        System.out.println(
            "Preferences saved for: " + customerId);
    }

    // ─────────────────────────────────────────
    // DB — CREATE DEFAULT ROW ON REGISTRATION
    // ─────────────────────────────────────────
    public static void createPreferences(int customerId)
        throws ClassNotFoundException, SQLException {
        savePreferences(customerId, 1, 1, 1, "", "");
    }

    // ─────────────────────────────────────────
    // HELPERS — notification toggles
    // ─────────────────────────────────────────
    private void setNotifToggle(ToggleButton btn, boolean on) {
        btn.setSelected(on);
        btn.setStyle(on ? TOGGLE_ON : TOGGLE_OFF);
    }

    private void updateNotifStyle(ToggleButton btn) {
        btn.setStyle(btn.isSelected() ? TOGGLE_ON : TOGGLE_OFF);
    }

    // ─────────────────────────────────────────
    // HELPERS — category buttons
    // ─────────────────────────────────────────
    private void setCatToggle(ToggleButton btn, boolean on) {
        btn.setSelected(on);
        btn.setStyle(on ? ON_STYLE : OFF_STYLE);
    }

    private void updateCatStyle(ToggleButton btn) {
        btn.setStyle(btn.isSelected() ? ON_STYLE : OFF_STYLE);
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
                deliveryInstructions.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
}