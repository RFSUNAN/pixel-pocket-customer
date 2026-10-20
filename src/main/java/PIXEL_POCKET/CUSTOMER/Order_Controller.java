package PIXEL_POCKET.CUSTOMER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Order_Controller {

    // ─────────────────────────────────────────
    // METHOD 1 - getOrderHistory()
    // ─────────────────────────────────────────
    public static void getOrderHistory(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM ORDERS " +
                     "WHERE customer_id = ? " +
                     "ORDER BY order_date DESC";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        System.out.println("------ ORDER HISTORY ------");
        boolean hasOrders = false;

        while (rs.next()) {
            hasOrders = true;
            System.out.println("Product:      " + rs.getString("product_name"));
            System.out.println("Order Number: " + rs.getString("order_number"));
            System.out.println("Price:        £" + rs.getDouble("total_amount"));
            System.out.println("Attributes:   " + rs.getString("attributes"));
            System.out.println("Status:       " + rs.getString("status"));
            System.out.println("---------------------------");
        }

        if (!hasOrders) System.out.println("No order history found.");
        rs.close(); ps.close(); c.close();
    }

    // ─────────────────────────────────────────
    // METHOD 2 - getTotalSpent()
    // ─────────────────────────────────────────
    public static double getTotalSpent(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT SUM(total_amount) FROM ORDERS " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        double total = 0.0;
        if (rs.next()) total = rs.getDouble(1);

        rs.close(); ps.close(); c.close();
        System.out.println("TOTAL: £" + total);
        return total;
    }

    // ─────────────────────────────────────────
    // METHOD 3 - hasActiveOrders()
    // ─────────────────────────────────────────
    public static boolean hasActiveOrders(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT COUNT(*) FROM ORDERS " +
                     "WHERE customer_id = ? " +
                     "AND status IN ('Processing','Shipped')";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        int count = rs.getInt(1);
        rs.close(); ps.close(); c.close();

        if (count > 0) {
            System.out.println("Active orders exist: " + count);
            return true;
        }
        System.out.println("No active orders.");
        return false;
    }

    // ─────────────────────────────────────────
    // METHOD 4 - addDummyOrders()
    // ─────────────────────────────────────────
    public static void addDummyOrders(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String[][] orders = {
            {"Burberry Harrington Jacket",
             "214558-510-BUR","1490.00",
             "Color: Black, Size: XS","Delivered","2025-01-15"},
            {"Dior Sauvage Eau de Toilette",
             "215848-548-DIO","85.00",
             "Size: 50 ml","Processing","2025-02-20"},
            {"Gymshark Lifting Hoodie",
             "124892-328-GSH","55.00",
             "Color: Black, Size: XS","Shipped","2025-03-01"},
            {"Nike Dunk Low Retro",
             "648120-407-NDL","109.95",
             "Color: Olive, Size: 7","Delivered","2024-12-10"}
        };

        String SQL = "INSERT INTO ORDERS " +
                     "(customer_id, product_name, order_number, " +
                     "total_amount, attributes, status, order_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        for (String[] order : orders) {
            PreparedStatement ps = c.prepareStatement(SQL);
            ps.setInt(1, customerId);
            ps.setString(2, order[0]);
            ps.setString(3, order[1]);
            ps.setDouble(4, Double.parseDouble(order[2]));
            ps.setString(5, order[3]);
            ps.setString(6, order[4]);
            ps.setString(7, order[5]);
            ps.executeUpdate();
            ps.close();
        }

        c.close();
        System.out.println("Dummy orders added for ID: " + customerId);
    }

    // ─────────────────────────────────────────
    // METHOD 5 - ordersExist()
    // ─────────────────────────────────────────
    public static boolean ordersExist(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT COUNT(*) FROM ORDERS " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        int count = rs.getInt(1);
        rs.close(); ps.close(); c.close();
        return count > 0;
    }

    // ─────────────────────────────────────────
    // METHOD 6 - getOrdersByCustomer()
    // Returns list of orders for the UI
    // Each String[] = [orderNumber,
    //   productName, price, color,
    //   size, status]
    // Parses "attributes" column which is
    // stored as "Color: Black, Size: XS"
    // ─────────────────────────────────────────
    public static List<String[]> getOrdersByCustomer(int customerId)
        throws ClassNotFoundException, SQLException {

        List<String[]> orders = new ArrayList<>();

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT product_name, order_number, " +
                     "total_amount, attributes, status " +
                     "FROM ORDERS " +
                     "WHERE customer_id = ? " +
                     "ORDER BY order_date DESC";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String productName  = rs.getString("product_name");
            String orderNumber  = rs.getString("order_number");
            String price        = String.valueOf(
                                      rs.getDouble("total_amount"));
            String attributes   = rs.getString("attributes");
            String status       = rs.getString("status");

            // PARSE attributes column
            // format: "Color: Black, Size: XS"
            // or just "Size: 50 ml"
            String color = "";
            String size  = "";

            if (attributes != null && !attributes.isEmpty()) {
                String[] parts = attributes.split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.toLowerCase().startsWith("color:")) {
                        color = part.substring(
                            part.indexOf(":") + 1).trim();
                    } else if (part.toLowerCase().startsWith("size:")) {
                        size = part.substring(
                            part.indexOf(":") + 1).trim();
                    }
                }
            }

            orders.add(new String[]{
                orderNumber != null ? orderNumber : "",
                productName != null ? productName : "",
                price,
                color,
                size,
                status != null ? status : ""
            });
        }

        rs.close(); ps.close(); c.close();
        return orders;
    }
}