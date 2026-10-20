package PIXEL_POCKET.CUSTOMER;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminHome_Controller {

    // ─────────────────────────────────────────
    // STATS
    // ─────────────────────────────────────────
    @FXML private Label statTotal;
    @FXML private Label statActive;
    @FXML private Label statDeactivated;

    // ─────────────────────────────────────────
    // CUSTOMERS TABLE
    // ─────────────────────────────────────────
    @FXML private TextField                    searchField;
    @FXML private TableView<String[]>          customersTable;
    @FXML private TableColumn<String[],String> colId;
    @FXML private TableColumn<String[],String> colName;
    @FXML private TableColumn<String[],String> colEmail;
    @FXML private TableColumn<String[],String> colStatus;

    // ─────────────────────────────────────────
    // EDIT PANEL
    // ─────────────────────────────────────────
    @FXML private VBox      editPanel;
    @FXML private Label     editingLabel;
    @FXML private TextField emailField;
    @FXML private TextField userIdField;
    @FXML private Label     currentIdLabel;
    @FXML private Label     newIdLabel;
    @FXML private Label     statusLabel;
    @FXML private Button    toggleBtn;
    @FXML private Label     errorLabel;
    @FXML private Label     successLabel;

    // ─────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────
    private int     selectedId     = -1;
    private boolean isActive       = true;
    private File    newIdFile      = null;

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {
        // SETUP COLUMNS
        colId.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue()[1]));
        colEmail.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue()[3]));

        loadStats();
        loadTable(null);
    }

    // ─────────────────────────────────────────
    // LOAD STATS
    // ─────────────────────────────────────────
    private void loadStats() {
        try {
            Connection c = CONTROLLER.connect();

            ResultSet r1 = c.createStatement()
                .executeQuery(
                    "SELECT COUNT(*) FROM CUSTOMERS");
            if (r1.next())
                statTotal.setText(
                    String.valueOf(r1.getInt(1)));

            ResultSet r2 = c.createStatement()
                .executeQuery(
                    "SELECT COUNT(*) FROM CUSTOMERS " +
                    "WHERE is_active=1");
            if (r2.next())
                statActive.setText(
                    String.valueOf(r2.getInt(1)));

            ResultSet r3 = c.createStatement()
                .executeQuery(
                    "SELECT COUNT(*) FROM CUSTOMERS " +
                    "WHERE is_active=0");
            if (r3.next())
                statDeactivated.setText(
                    String.valueOf(r3.getInt(1)));

            c.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // LOAD TABLE
    // ─────────────────────────────────────────
    private void loadTable(String filter) {
        try {
            ObservableList<String[]> data =
                FXCollections.observableArrayList();
            Connection c = CONTROLLER.connect();
            PreparedStatement ps;

            if (filter == null || filter.isEmpty()) {
                ps = c.prepareStatement(
                    "SELECT customer_id, name, email, " +
                    "is_active FROM CUSTOMERS " +
                    "ORDER BY customer_id DESC");
            } else {
                String like = "%" + filter + "%";
                ps = c.prepareStatement(
                    "SELECT customer_id, name, email, " +
                    "is_active FROM CUSTOMERS " +
                    "WHERE name LIKE ? " +
                    "OR email LIKE ? " +
                    "OR contact_number LIKE ? " +
                    "ORDER BY customer_id DESC");
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new String[]{
                    String.valueOf(
                        rs.getInt("customer_id")),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("is_active") == 1
                        ? "Active" : "Deactivated"
                });
            }
            rs.close(); ps.close(); c.close();
            customersTable.setItems(data);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────
    @FXML
    public void handleSearch() {
        loadTable(searchField.getText().trim());
    }

    @FXML
    public void handleShowAll() {
        searchField.clear();
        loadTable(null);
    }

    // ─────────────────────────────────────────
    // ROW CLICK — load into edit panel
    // ─────────────────────────────────────────
    @FXML
    public void handleRowClick() {
        String[] row = customersTable
            .getSelectionModel().getSelectedItem();
        if (row == null) return;

        selectedId = Integer.parseInt(row[0]);
        isActive   = "Active".equals(row[3]);

        // LOAD FULL DATA
        try {
            Connection c = CONTROLLER.connect();
            PreparedStatement ps = c.prepareStatement(
                "SELECT email, id_card_path " +
                "FROM CUSTOMERS " +
                "WHERE customer_id = ?"
            );
            ps.setInt(1, selectedId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                emailField.setText(
                    rs.getString("email"));
                userIdField.setText(
                    "Severus" + selectedId + "@#1");
                String idPath =
                    rs.getString("id_card_path");
                currentIdLabel.setText(
                    (idPath != null && !idPath.isEmpty())
                        ? idPath : "No file on record");
            }
            rs.close(); ps.close(); c.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        editingLabel.setText(
            "Editing: " + row[1] +
            "  (ID: " + selectedId + ")");
        newIdFile = null;
        newIdLabel.setText("");
        errorLabel.setText("");
        successLabel.setText("");
        refreshToggleBtn();

        editPanel.setVisible(true);
        editPanel.setManaged(true);
    }

    // ─────────────────────────────────────────
    // UPLOAD NEW ID CARD
    // ─────────────────────────────────────────
    @FXML
    public void handleIdUpload() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select ID Document");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "ID Files",
                "*.jpg", "*.jpeg",
                "*.png", "*.pdf")
        );
        File f = fc.showOpenDialog(
            emailField.getScene().getWindow());
        if (f == null) return;

        if (f.length() > 5L * 1024 * 1024) {
            errorLabel.setText(
                "File exceeds 5 MB limit.");
            return;
        }
        newIdFile = f;
        newIdLabel.setText("✔ " + f.getName());
        errorLabel.setText("");
    }

    // ─────────────────────────────────────────
    // TOGGLE ACTIVE / DEACTIVATED
    // ─────────────────────────────────────────
    @FXML
    public void handleToggle() {
        if (selectedId == -1) return;
        try {
            int newStatus = isActive ? 0 : 1;
            Connection c = CONTROLLER.connect();
            PreparedStatement ps = c.prepareStatement(
                "UPDATE CUSTOMERS SET is_active = ? " +
                "WHERE customer_id = ?"
            );
            ps.setInt(1, newStatus);
            ps.setInt(2, selectedId);
            ps.executeUpdate();
            ps.close(); c.close();

            isActive = (newStatus == 1);
            refreshToggleBtn();
            successLabel.setText(
                isActive
                    ? "✔ Account reactivated!"
                    : "✔ Account deactivated!");
            loadStats();
            loadTable(null);
        } catch (Exception e) {
            errorLabel.setText(
                "Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // SAVE CHANGES
    // ─────────────────────────────────────────
    @FXML
    public void handleSave() {
        errorLabel.setText("");
        successLabel.setText("");

        if (selectedId == -1) return;

        String newEmail =
            emailField.getText().trim();

        // VALIDATE EMAIL
        if (newEmail.isEmpty()
                || !newEmail.contains("@")) {
            errorLabel.setText(
                "Please enter a valid email.");
            return;
        }

        try {
            Connection c = CONTROLLER.connect();

            // CHECK EMAIL NOT ALREADY TAKEN
            PreparedStatement check =
                c.prepareStatement(
                    "SELECT customer_id FROM CUSTOMERS " +
                    "WHERE email = ? " +
                    "AND customer_id != ?"
                );
            check.setString(1, newEmail);
            check.setInt(2, selectedId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                errorLabel.setText(
                    "Email already used by " +
                    "another customer.");
                rs.close(); check.close();
                c.close();
                return;
            }
            rs.close(); check.close();

            // UPDATE EMAIL
            PreparedStatement ps1 = c.prepareStatement(
                "UPDATE CUSTOMERS SET email = ? " +
                "WHERE customer_id = ?"
            );
            ps1.setString(1, newEmail);
            ps1.setInt(2, selectedId);
            ps1.executeUpdate();
            ps1.close();

            // UPDATE ID CARD IF NEW FILE
            if (newIdFile != null) {
                PreparedStatement ps2 =
                    c.prepareStatement(
                        "UPDATE CUSTOMERS " +
                        "SET id_card_path = ? " +
                        "WHERE customer_id = ?"
                    );
                ps2.setString(1,
                    newIdFile.getAbsolutePath());
                ps2.setInt(2, selectedId);
                ps2.executeUpdate();
                ps2.close();
                currentIdLabel.setText(
                    newIdFile.getAbsolutePath());
                newIdFile = null;
                newIdLabel.setText("");
            }

            c.close();

            successLabel.setText(
                "✔ Saved successfully!");
            loadTable(null);

        } catch (Exception e) {
            errorLabel.setText(
                "Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // CANCEL
    // ─────────────────────────────────────────
    @FXML
    public void handleCancel() {
        editPanel.setVisible(false);
        editPanel.setManaged(false);
        selectedId = -1;
        customersTable.getSelectionModel()
            .clearSelection();
    }

    // ─────────────────────────────────────────
    // REFRESH TOGGLE BUTTON STYLE
    // ─────────────────────────────────────────
    private void refreshToggleBtn() {
        if (isActive) {
            statusLabel.setText("Active");
            statusLabel.setStyle(
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4CAF50;");
            toggleBtn.setText("Deactivate");
            toggleBtn.setStyle(
                "-fx-background-color: #F44336;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 14;");
        } else {
            statusLabel.setText("Deactivated");
            statusLabel.setStyle(
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #F44336;");
            toggleBtn.setText("Reactivate");
            toggleBtn.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 14;");
        }
    }

    // ─────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────
    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("AdminLogin.fxml"));
            Stage stage = (Stage)
                statTotal.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 750));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}