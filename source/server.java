import java.net.*;
import java.io.*;

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

	static final String admin = "jnewhouse";
	static final String adminPwd = "CrOliNAr";

	public static void main(String[] args) {
		Connection con = dbConnect();
		if(con == null) {
			System.err.println("Error connecting to database");
			System.exit(1);
		}

		boolean listening = true;
		try(ServerSocket ss = new ServerSocket(PORT, MAX_CON)) {
			System.out.println("LISTENING ON PORT: " + PORT);
			while(listening) {
				ServerThread st = new ServerThread(ss.accept(), con);
				st.start();
			}
		}
		catch(IOException e) {
			System.err.println("Could not listen on port " + PORT);
			System.exit(-1);
		}

		try {
			con.close();
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.exit(1);
		}
	}

	public static Connection
	dbConnect() {
		try {
			Class.forName(dbDriver);
		}
		catch(Exception ex) {
			System.out.println(ex);
			return null;
		}

		try {
			return DriverManager.getConnection(url, admin, adminPwd);
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		}
	}
}
