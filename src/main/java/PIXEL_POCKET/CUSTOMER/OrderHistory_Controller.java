package PIXEL_POCKET.CUSTOMER;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderHistory_Controller {

    // ─────────────────────────────────────────
    // FXML FIELDS
    // ─────────────────────────────────────────
    @FXML private Label totalLabel;
    @FXML private VBox  orderListContainer;

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @FXML
    public void initialize() {
        int customerId = Session.getCustomerId();
        try {
            // USE getTotalSpent() — already in your controller
            double total =
                Order_Controller.getTotalSpent(customerId);
            totalLabel.setText(
                String.format("TOTAL : £%,.2f", total));

            // GET ALL ORDERS
            List<String[]> orders =
                Order_Controller.getOrdersByCustomer(customerId);

            if (orders.isEmpty()) {
                Label empty = new Label("No orders found.");
                empty.setStyle(
                    "-fx-text-fill: #C3C8C9;" +
                    "-fx-font-size: 16;" +
                    "-fx-padding: 30;"
                );
                orderListContainer.getChildren().add(empty);
                return;
            }

            for (String[] order : orders) {
                // order[] = [orderNumber, productName,
                //            price, color, size, status]
                HBox row = buildOrderRow(
                    order[1], // productName
                    order[0], // orderNumber
                    order[2], // price
                    order[3], // color
                    order[4], // size
                    order[5]  // status
                );
                orderListContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            Label err = new Label(
                "Could not load orders: " + e.getMessage());
            err.setStyle(
                "-fx-text-fill: #ff6b6b; -fx-font-size: 14;" +
                "-fx-padding: 20;");
            orderListContainer.getChildren().add(err);
        }
    }

    // ─────────────────────────────────────────
    // BUILD ONE ORDER ROW
    // ─────────────────────────────────────────
    private HBox buildOrderRow(
            String productName,
            String orderNumber,
            String price,
            String color,
            String size,
            String status) {

        HBox row = new HBox();
        row.setPrefHeight(90);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: rgba(56,84,89,0.6);" +
            "-fx-border-color: transparent transparent " +
            "rgba(195,200,201,0.15) transparent;" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-padding: 0 20;"
        );

        // ── PRODUCT IMAGE PLACEHOLDER ────────
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(65, 65);
        imgBox.setMinSize(65, 65);
        imgBox.setMaxSize(65, 65);
        imgBox.setStyle(
            "-fx-background-color: rgba(195,200,201,0.25);" +
            "-fx-background-radius: 10;"
        );
        Label imgIcon = new Label("🖼");
        imgIcon.setStyle("-fx-font-size: 28;");
        imgBox.getChildren().add(imgIcon);

        // ── PRODUCT NAME ─────────────────────
        Label nameLabel = new Label(productName);
        nameLabel.setPrefWidth(340);
        nameLabel.setWrapText(true);
        nameLabel.setStyle(
            "-fx-font-family: 'Jaldi';" +
            "-fx-font-size: 16;" +
            "-fx-text-fill: #C3C8C9;" +
            "-fx-padding: 0 0 0 16;"
        );

        // ── ORDER NUMBER ─────────────────────
        Label orderLabel = new Label(orderNumber);
        orderLabel.setPrefWidth(200);
        orderLabel.setStyle(
            "-fx-font-family: 'Jaldi';" +
            "-fx-font-size: 15;" +
            "-fx-text-fill: #C3C8C9;"
        );

        // ── PRICE ────────────────────────────
        // Format price nicely e.g. £1,490.00
        String priceFormatted;
        try {
            double p = Double.parseDouble(price);
            priceFormatted = String.format("£%,.2f", p);
        } catch (NumberFormatException e) {
            priceFormatted = "£" + price;
        }
        Label priceLabel = new Label(priceFormatted);
        priceLabel.setPrefWidth(120);
        priceLabel.setStyle(
            "-fx-font-family: 'Jaldi';" +
            "-fx-font-size: 15;" +
            "-fx-text-fill: #C3C8C9;"
        );

        // ── ATTRIBUTES ───────────────────────
        VBox attrBox = new VBox(2);
        attrBox.setPrefWidth(160);
        attrBox.setAlignment(Pos.CENTER_LEFT);

        if (!color.isEmpty()) {
            HBox colorRow = new HBox(4);
            colorRow.setAlignment(Pos.CENTER_LEFT);
            Label colorLbl = new Label("Color: ");
            colorLbl.setStyle(
                "-fx-font-size: 13;" +
                "-fx-text-fill: #C3C8C9;");
            Label colorVal = new Label(color);
            colorVal.setStyle(
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + getColorHex(color) + ";"
            );
            colorRow.getChildren().addAll(colorLbl, colorVal);
            attrBox.getChildren().add(colorRow);
        }

        if (!size.isEmpty()) {
            Label sizeLbl = new Label("Size: " + size);
            sizeLbl.setStyle(
                "-fx-font-size: 13;" +
                "-fx-text-fill: #C3C8C9;");
            attrBox.getChildren().add(sizeLbl);
        }

        // ── STATUS BADGE ─────────────────────
        Label statusLabel = new Label(status);
        statusLabel.setPrefWidth(110);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle(
            "-fx-background-color: " +
            getStatusColor(status) + ";" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 5 12;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        row.getChildren().addAll(
            imgBox, nameLabel, orderLabel,
            priceLabel, attrBox, statusLabel
        );
        return row;
    }

    // ─────────────────────────────────────────
    // STATUS COLOUR
    // ─────────────────────────────────────────
    private String getStatusColor(String status) {
        if (status == null) return "#888888";
        return switch (status.toLowerCase()) {
            case "delivered"  -> "#4CAF50";
            case "shipped"    -> "#8BC34A";
            case "processing" -> "#FF9800";
            case "cancelled"  -> "#F44336";
            case "pending"    -> "#2196F3";
            default           -> "#888888";
        };
    }

    // ─────────────────────────────────────────
    // COLOUR ATTRIBUTE HEX
    // ─────────────────────────────────────────
    private String getColorHex(String color) {
        if (color == null) return "#C3C8C9";
        return switch (color.toLowerCase()) {
            case "black"        -> "#1a1a1a";
            case "white"        -> "#f5f5f5";
            case "red"          -> "#e53935";
            case "blue"         -> "#1e88e5";
            case "green"        -> "#43a047";
            case "olive"        -> "#afb42b";
            case "grey", "gray" -> "#9e9e9e";
            case "navy"         -> "#1a237e";
            case "brown"        -> "#6d4c41";
            case "pink"         -> "#e91e63";
            case "yellow"       -> "#fdd835";
            case "orange"       -> "#fb8c00";
            case "purple"       -> "#8e24aa";
            default             -> "#C3C8C9";
        };
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
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}