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
	private PrintWriter out = null;
	private BufferedReader in = null;

	public ServerThread(Socket s, Connection con) {
		super("ServerThread");
		this.s = s;
		this.con = con;

		try {
			this.out = new PrintWriter(this.s.getOutputStream(), true);
			this.in = new BufferedReader(
								new InputStreamReader(this.s.getInputStream()));
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}

	public void run() {
		System.out.println(s.getInetAddress().getHostAddress() + ":connected");

		if(this.authenticate()) {
			//Call db processing method
		}

		this.shutdown();
	}

	private boolean
	authenticate() {
		boolean ret_val = false;

		String password = null;
		try {
			this.userName = this.in.readLine();
			password = this.in.readLine();
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}

		if(this.userName.equals("admin") && password.equals("shutdown")) {
			System.out.println("SHUTTING DOWN");
			out.println("unavailable");
			this.shutdown();
			System.exit(0);
		}

		Statement smt = null;
		ResultSet rs = null;
		try {
			smt = this.con.createStatement();
			rs = smt.executeQuery("SELECT password FROM Users WHERE username=\""
								+ this.userName + "\"");

			if(rs.first())
				if(password.equals(rs.getString(1))) {
					out.println("1");
					ret_val = true;
				}
				else {
					out.println("0");
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

	private void
	shutdown() {
		try {
			if(this.out != null)
				this.out.close();
			if(this.in != null)
				this.in.close();

			System.out.println(s.getInetAddress().getHostAddress()
															+ ":disconnected");
			this.s.close();
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}
}
