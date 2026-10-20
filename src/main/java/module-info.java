module PIXEL_POCKET.CUSTOMER {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    opens PIXEL_POCKET.CUSTOMER to javafx.fxml;
    exports PIXEL_POCKET.CUSTOMER;
}