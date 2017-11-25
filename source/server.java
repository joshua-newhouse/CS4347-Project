import java.net.*;
import java.io.*;
import java.lang.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class server {
	static final int PORT = 10001;
	static final int MAX_CON = 4;

	static final String dbDriver = "com.mysql.jdbc.Driver";
	static final String	url =
		"jdbc:mysql://" + 
		"cs4347-project.cujq9m2vjohw.us-east-1.rds.amazonaws.com:3306/finance" +
		"?autoReconnect=true&useSSL=false";
	static Connection con = null;

	static final String admin = "jnewhouse";
	static final String adminPwd = "CrOliNAr";

	public static void main(String[] args) {
		try {
			con = dbConnect();
			ServerSocket ss = new ServerSocket(PORT, MAX_CON);
			System.out.println("LISTENING ON PORT: " + PORT);

			while(true) {
				ServerThread st = new ServerThread(ss.accept(), con);
				st.start();
			}
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				con.close();
				System.out.println("Disconnected from database");
			}
			catch(SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
				System.exit(1);
			}

			System.exit(0);
		}
	}

	public static Connection
	dbConnect() throws Exception {
		Class.forName(dbDriver);
		return DriverManager.getConnection(url, admin, adminPwd);
	}
}
