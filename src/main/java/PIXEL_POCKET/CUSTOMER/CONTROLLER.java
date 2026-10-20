package PIXEL_POCKET.CUSTOMER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CONTROLLER {
	
	
public static Connection connect () throws ClassNotFoundException, SQLException

{
	
System.out.println("CONNECTING..........");

Connection c = null;

// LOAD THE SQL DRIVER

Class.forName("org.sqlite.JDBC") ;


// OPEN THE DATABASE 


c = DriverManager.getConnection("jdbc:sqlite:pixelpocket.db");

// TURN ON FORGEIN KEY

Statement stmnt = c.createStatement();

stmnt.execute("PRAGMA foreign_keys = ON");
stmnt.close();

System.out.println("CONNECTED");
return c ;
}


}	
	
	
	
	
	
	
	
	
	
	


