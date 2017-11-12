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

	public static void main(String[] args) {
		Connection con = dbConnect();
		if(con == null) {
			System.err.println("Error connecting to database");
			System.exit(1);
		}

		boolean listening = true;
		try(ServerSocket ss = new ServerSocket(PORT, MAX_CON)) {
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
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(Exception ex) {
			System.out.println(ex);
			return null;
		}

		try {
			String url = "jdbc:mysql://localhost:3306/finance" +
						"?autoReconnect=true&useSSL=false";
			return DriverManager.getConnection(url, "root", "1qazXSW@3edcVFR$");
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		}
	}
}
