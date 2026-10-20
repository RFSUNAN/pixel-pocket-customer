package PIXEL_POCKET.CUSTOMER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer_Controller {

    // ─────────────────────────────────────────
    // METHOD 1 - registerCustomer()
    // ─────────────────────────────────────────
    public static void registerCustomer(
        String firstName, String lastName,
        String dateOfBirth, String gender,
        String street, String city,
        String postcode, String county, String country,
        String phone, String email, String password,
        String cardNumber, String sortCode, String cvv,
        String idCardPath)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "INSERT INTO CUSTOMERS " +
                     "(first_name, last_name, name, " +
                     "date_of_birth, gender, " +
                     "delivery_address, postcode, county, country, " +
                     "contact_number, email, password, " +
                     "card_number, sort_code, cvv, " +
                     "id_card_path) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1,  firstName);
        ps.setString(2,  lastName);
        ps.setString(3,  firstName + " " + lastName);
        ps.setString(4,  dateOfBirth);
        ps.setString(5,  gender);
        ps.setString(6,  street + ", " + city);
        ps.setString(7,  postcode);
        ps.setString(8,  county);
        ps.setString(9,  country);
        ps.setString(10, phone);
        ps.setString(11, email);
        ps.setString(12, password);
        ps.setString(13, cardNumber);
        ps.setString(14, sortCode);
        ps.setString(15, cvv);
        ps.setString(16, idCardPath);

        ps.executeUpdate();
        ps.close();
        c.close();
        System.out.println("Customer Registered: " + email);
    }

    // ─────────────────────────────────────────
    // METHOD 2 - loginCustomer()
    // ─────────────────────────────────────────
    public static int loginCustomer(String email, String password)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT customer_id FROM CUSTOMERS " +
                     "WHERE email = ? AND password = ? AND is_active = 1";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("customer_id");
            rs.close(); ps.close(); c.close();
            System.out.println("Login Successful! ID: " + id);
            return id;
        }

        rs.close(); ps.close(); c.close();
        System.out.println("Login Failed!");
        return -1;
    }

    // ─────────────────────────────────────────
    // METHOD 3 - updateCustomer()
    // ─────────────────────────────────────────
    public static void updateCustomer(
        int customerId,
        String firstName, String lastName,
        String dateOfBirth, String gender,
        String street, String city,
        String postcode, String county, String country,
        String phone, String email,
        String cardNumber, String sortCode, String cvv,
        String idCardPath)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "UPDATE CUSTOMERS SET " +
                     "first_name = ?, last_name = ?, name = ?, " +
                     "date_of_birth = ?, gender = ?, " +
                     "delivery_address = ?, postcode = ?, " +
                     "county = ?, country = ?, contact_number = ?, " +
                     "email = ?, " +
                     "card_number = ?, sort_code = ?, cvv = ?, " +
                     "id_card_path = ? " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1,  firstName);
        ps.setString(2,  lastName);
        ps.setString(3,  firstName + " " + lastName);
        ps.setString(4,  dateOfBirth);
        ps.setString(5,  gender);
        ps.setString(6,  street + ", " + city);
        ps.setString(7,  postcode);
        ps.setString(8,  county);
        ps.setString(9,  country);
        ps.setString(10, phone);
        ps.setString(11, email);
        ps.setString(12, cardNumber);
        ps.setString(13, sortCode);
        ps.setString(14, cvv);
        ps.setString(15, idCardPath);
        ps.setInt(16,    customerId);

        ps.executeUpdate();
        ps.close();
        c.close();
        System.out.println("Customer Updated: " + customerId);
    }

    // ─────────────────────────────────────────
    // METHOD 4 - deactivateCustomer()
    // ─────────────────────────────────────────
    public static void deactivateCustomer(int customerId)
        throws ClassNotFoundException, SQLException {

        boolean hasActive = Order_Controller.hasActiveOrders(customerId);

        if (hasActive) {
            System.out.println("DEACTIVATION BLOCKED!");
            System.out.println("Reason: You have active orders.");
            return;
        }

        Connection c = CONTROLLER.connect();

        String SQL = "UPDATE CUSTOMERS SET is_active = 0 " +
                     "WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);
        ps.executeUpdate();
        ps.close();
        c.close();
        System.out.println("Account Deactivated for ID: " + customerId);
    }

    // ─────────────────────────────────────────
    // METHOD 5 - getCustomer()
    // ─────────────────────────────────────────
    public static void getCustomer(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM CUSTOMERS WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("------ CUSTOMER DETAILS ------");
            System.out.println("ID:       " + rs.getInt("customer_id"));
            System.out.println("Name:     " + rs.getString("first_name")
                               + " "        + rs.getString("last_name"));
            System.out.println("DOB:      " + rs.getString("date_of_birth"));
            System.out.println("Gender:   " + rs.getString("gender"));
            System.out.println("Address:  " + rs.getString("delivery_address"));
            System.out.println("Postcode: " + rs.getString("postcode"));
            System.out.println("Phone:    " + rs.getString("contact_number"));
            System.out.println("Email:    " + rs.getString("email"));
            System.out.println("Active:   " + rs.getInt("is_active"));
            System.out.println("------------------------------");
        } else {
            System.out.println("Customer Not Found!");
        }

        rs.close(); ps.close(); c.close();
    }

    // ─────────────────────────────────────────
    // METHOD 6 - emailExists()
    // ─────────────────────────────────────────
    public static boolean emailExists(String email)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT COUNT(*) FROM CUSTOMERS WHERE email = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();
        int count = rs.getInt(1);

        rs.close(); ps.close(); c.close();
        return count > 0;
    }

    // ─────────────────────────────────────────
    // METHOD 7 - getCustomerById()
    // Returns all fields as String array
    // for EditProfile to display
    // ─────────────────────────────────────────
    public static String[] getCustomerById(int customerId)
        throws ClassNotFoundException, SQLException {

        Connection c = CONTROLLER.connect();

        String SQL = "SELECT * FROM CUSTOMERS WHERE customer_id = ?";

        PreparedStatement ps = c.prepareStatement(SQL);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String[] data = new String[15];

            data[0]  = rs.getString("first_name");
            data[1]  = rs.getString("last_name");
            data[2]  = rs.getString("date_of_birth");
            data[3]  = rs.getString("gender");

            // SPLIT delivery_address back into street + city
            String fullAddress = rs.getString("delivery_address");
            if (fullAddress != null && fullAddress.contains(", ")) {
                String[] parts = fullAddress.split(", ", 2);
                data[4] = parts[0]; // street
                data[5] = parts[1]; // city
            } else {
                data[4] = fullAddress != null ? fullAddress : "";
                data[5] = "";
            }

            data[6]  = rs.getString("postcode");
            data[7]  = rs.getString("county");
            data[8]  = rs.getString("country");
            data[9]  = rs.getString("contact_number");
            data[10] = rs.getString("email");
            data[11] = "Severus" + customerId + "@#1"; // generated User ID
            data[12] = rs.getString("card_number") != null
                       ? rs.getString("card_number") : "";
            data[13] = rs.getString("sort_code") != null
                       ? rs.getString("sort_code") : "";
            data[14] = rs.getString("cvv") != null
                       ? rs.getString("cvv") : "";

            rs.close(); ps.close(); c.close();
            return data;
        }

        rs.close(); ps.close(); c.close();
        return null;
    }
}