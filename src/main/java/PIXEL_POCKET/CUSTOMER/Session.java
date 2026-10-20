package PIXEL_POCKET.CUSTOMER;

// Session.java
// Keeps track of who is currently logged in
// Any screen can call Session.getCustomerId()

public class Session {

    // -1 means nobody is logged in
    private static int customerId = -1;
    private static int adminId    = -1;

    // ─────────────────────────────────────────
    // CUSTOMER SESSION
    // ─────────────────────────────────────────

    // Call when customer logs in
    public static void setCustomerId(int id) {
        customerId = id;
        System.out.println("Customer session started: " + id);
    }

    // Call to get logged in customer ID
    public static int getCustomerId() {
        return customerId;
    }

    // Call when customer logs out
    public static void clearCustomerSession() {
        customerId = -1;
        System.out.println("Customer session cleared.");
    }

    // ─────────────────────────────────────────
    // ADMIN SESSION
    // ─────────────────────────────────────────

    // Call when admin logs in
    public static void setAdminId(int id) {
        adminId = id;
        System.out.println("Admin session started: " + id);
    }

    // Call to get logged in admin ID
    public static int getAdminId() {
        return adminId;
    }

    // Call when admin logs out
    public static void clearAdminSession() {
        adminId = -1;
        System.out.println("Admin session cleared.");
    }

}