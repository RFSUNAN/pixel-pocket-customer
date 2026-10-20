package PIXEL_POCKET.CUSTOMER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin_Controller {
/*
 
 
 METHOD 1 --- ADMMIN LOGIN 
 CHECKS IF USERNAME AND PASSWORD ARE SAME 
 
 
 */
    public static int loginAdmin(String username, String password)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT admin_id FROM ADMINS " +
                     "WHERE username = ? " +
                     "AND password = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("admin_id");
            rs.close();
            ps.close();
            c.close();
            System.out.println("Admin Login Successful! ID: " + id);
            return id;
        }

        rs.close();
        ps.close();
        c.close();
        System.out.println("Admin Login Failed!");
        return -1;
    }

   /*
    
    GETS ALL CUSTOMERS USED IN ADMIN USERS TABLE
    
    
    */
    
    public static void getAllCustomers()
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM CUSTOMERS " +
                     "ORDER BY customer_id";

        PreparedStatement ps = c.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();

        System.out.println("------ ALL CUSTOMERS ------");

        boolean hasCustomers = false;
        while (rs.next()) {
            hasCustomers = true;
            System.out.println("ID:      " + rs.getInt("customer_id"));
            System.out.println("Name:    " + rs.getString("first_name")
                               + " " + rs.getString("last_name"));
            System.out.println("Email:   " + rs.getString("email"));
            System.out.println("Active:  " + rs.getInt("is_active"));
            System.out.println("---------------------------");
        }

        if (!hasCustomers) {
            System.out.println("No customers found.");
        }

        rs.close();
        ps.close();
        c.close();
    }

    /*
     
      UPDATE CUSTOMER EMAIL AND PASSWORD
      
     
     */
    
    public static void updateCustomerEmail(
        int adminId, int customerId, String newEmail)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        // CHECK NEW EMAIL NOT ALREADY IN USE
        
        String checkSQL = "SELECT COUNT(*) FROM CUSTOMERS " +
                          "WHERE email = ?";

        PreparedStatement checkPs = c.prepareStatement(checkSQL);
        checkPs.setString(1, newEmail);
        ResultSet rs = checkPs.executeQuery();

        if (rs.getInt(1) > 0) {
            System.out.println("Email already in use: " + newEmail);
            rs.close();
            checkPs.close();
            c.close();
            return;
        }

        // UPDATE THE EMAIL
        
        String SQL = "UPDATE CUSTOMERS SET email = ? " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, newEmail);
        ps.setInt(2, customerId);
        ps.executeUpdate();

        // LOG THE ACTION
        
        logAction(c, adminId,
            "UPDATE_EMAIL",
            "customer_id=" + customerId +
            " new_email=" + newEmail);

        rs.close();
        checkPs.close();
        ps.close();
        c.close();

        System.out.println("Email updated for customer: " + customerId);
    }

   /*
    
    UPDATE CUSTOMER I CARD
    
    
    */
    
    public static void updateCustomerIdCard(
        int adminId, int customerId, String newIdCardPath)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "UPDATE CUSTOMERS SET id_card_path = ? " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, newIdCardPath);
        ps.setInt(2, customerId);
        ps.executeUpdate();

        // LOG THE ACTION
        
        logAction(c, adminId,
            "UPDATE_ID_CARD",
            "customer_id=" + customerId);

        ps.close();
        c.close();

        System.out.println("ID card updated for customer: " + customerId);
    }

   /*
    
    SEARCH CUSTOMER EMAIL ADDRESS
    
     
    */
    
    public static void searchCustomer(String searchTerm)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM CUSTOMERS " +
                     "WHERE name LIKE ? " +
                     "OR email LIKE ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, "%" + searchTerm + "%");
        ps.setString(2, "%" + searchTerm + "%");

        ResultSet rs = ps.executeQuery();

        System.out.println("------ SEARCH RESULTS ------");

        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("ID:     " + rs.getInt("customer_id"));
            System.out.println("Name:   " + rs.getString("first_name")
                               + " " + rs.getString("last_name"));
            System.out.println("Email:  " + rs.getString("email"));
            System.out.println("Active: " + rs.getInt("is_active"));
            System.out.println("----------------------------");
        }

        if (!found) {
            System.out.println("No customers found for: " + searchTerm);
        }

        rs.close();
        ps.close();
        c.close();
    }

   /*
    
    RECORD ADMIN LOG
    
    
    */
    
    private static void logAction(
        Connection c, int adminId,
        String action, String detail)
        throws SQLException {

        String SQL = "INSERT INTO AUDIT_LOG " +
                     "(admin_id, action, detail, timestamp) " +
                     "VALUES (?, ?, ?, datetime('now'))";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, adminId);
        ps.setString(2, action);
        ps.setString(3, detail);
        ps.executeUpdate();
        ps.close();

        System.out.println("Action logged: " + action);
    }

}