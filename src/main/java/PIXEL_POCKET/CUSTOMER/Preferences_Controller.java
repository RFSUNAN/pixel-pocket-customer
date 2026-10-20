package PIXEL_POCKET.CUSTOMER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Preferences_Controller {

    // ─────────────────────────────────────────
    // METHOD 1 - createPreferences()
    // Creates a default preferences row
    // Called when customer registers
    // ─────────────────────────────────────────
    public static void createPreferences(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "INSERT INTO PREFERENCES " +
                     "(customer_id, preferred_categories, " +
                     "email_notifications, sms_notifications, " +
                     "wishlist_notifications, " +
                     "delivery_instructions) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ps.setString(2, "");
        ps.setInt(3, 0);
        ps.setInt(4, 0);
        ps.setInt(5, 0);
        ps.setString(6, "");

        ps.executeUpdate();
        ps.close();
        c.close();

        System.out.println("Preferences Created for ID: " + customerId);
    }

    // ─────────────────────────────────────────
    // METHOD 2 - updatePreferences()
    // Saves all preferences from the screen
    // ─────────────────────────────────────────
    public static void updatePreferences(
        int customerId,
        int emailNotifications,
        int smsNotifications,
        int wishlistNotifications,
        String categories,
        String deliveryInstructions)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "UPDATE PREFERENCES SET " +
                     "email_notifications = ?, " +
                     "sms_notifications = ?, " +
                     "wishlist_notifications = ?, " +
                     "preferred_categories = ?, " +
                     "delivery_instructions = ? " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, emailNotifications);
        ps.setInt(2, smsNotifications);
        ps.setInt(3, wishlistNotifications);
        ps.setString(4, categories);
        ps.setString(5, deliveryInstructions);
        ps.setInt(6, customerId);

        ps.executeUpdate();
        ps.close();
        c.close();

        System.out.println("Preferences Saved for ID: " + customerId);
    }

    // ─────────────────────────────────────────
    // METHOD 3 - getPreferences()
    // Prints preferences for a customer
    // ─────────────────────────────────────────
    public static void getPreferences(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM PREFERENCES " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("------ PREFERENCES ------");
            System.out.println("Email Notif:    " + rs.getInt("email_notifications"));
            System.out.println("SMS Notif:      " + rs.getInt("sms_notifications"));
            System.out.println("Wishlist Notif: " + rs.getInt("wishlist_notifications"));
            System.out.println("Categories:     " + rs.getString("preferred_categories"));
            System.out.println("Delivery Note:  " + rs.getString("delivery_instructions"));
            System.out.println("-------------------------");
        } else {
            System.out.println("No Preferences Found!");
        }

        rs.close();
        ps.close();
        c.close();
    }

    // ─────────────────────────────────────────
    // METHOD 4 - getRawPreferences()
    // Returns preferences as a String array
    // Used by Preferences_View_Controller
    // to load saved values onto the screen
    // [0] email_notifications
    // [1] sms_notifications
    // [2] wishlist_notifications
    // [3] preferred_categories
    // [4] delivery_instructions
    // ─────────────────────────────────────────
    public static String[] getRawPreferences(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM PREFERENCES " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String[] prefs = {
                String.valueOf(rs.getInt("email_notifications")),
                String.valueOf(rs.getInt("sms_notifications")),
                String.valueOf(rs.getInt("wishlist_notifications")),
                rs.getString("preferred_categories"),
                rs.getString("delivery_instructions")
            };
            rs.close();
            ps.close();
            c.close();
            return prefs;
        }

        rs.close();
        ps.close();
        c.close();
        return null;
    }

 // ─────────────────────────────────────────
    // METHOD 5 - preferencesExists()
    // Checks if preferences already exist
    // Prevents duplicate preferences rows
    // ─────────────────────────────────────────
    public static boolean preferencesExists(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT COUNT(*) FROM PREFERENCES " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();

        int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();

        return count > 0;
    }

}