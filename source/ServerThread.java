import java.net.*;
import java.io.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ServerThread extends Thread {
	private Socket s = null;
	private Connection con = null;
	private String userName = null;

	public ServerThread(Socket s, Connection con) {
		super("ServerThread");
		this.s = s;
		this.con = con;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			out = new PrintWriter(this.s.getOutputStream(), true);
			in = new BufferedReader(
								new InputStreamReader(this.s.getInputStream()));
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}

		if(this.authenticate(out, in)) {
			//Call db processing method
		}
		else {
			out.println("Authentication Failed");
		}

		try {
			if(out != null)
				out.close();
			if(in != null)
				in.close();
			this.s.close();
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}

	private boolean
	authenticate(PrintWriter out, BufferedReader in) {
		boolean ret_val = false;

		out.println("authenticate");

		String password = null;
		try {
			this.userName = in.readLine();
			password = in.readLine();
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}

		Statement smt = null;
		ResultSet rs = null;
		try {
			smt = this.con.createStatement();
			rs = smt.executeQuery("SELECT password FROM Users WHERE username=\""
								+ this.userName + "\"");

			if(rs.first())
				if(password.equals(rs.getString(1))) {
					out.println("Authentication successful");
					ret_val = true;
				}
				else {
					out.println("Authentication failed");
					ret_val = false;
				}
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException ex) {
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}
			}

			if(smt != null) {
				try {
					smt.close();
				}
				catch(SQLException ex) {
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}
			}
		}

		return ret_val;
	}
}
