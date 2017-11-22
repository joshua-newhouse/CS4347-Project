import java.net.*;
import java.io.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ServerThread extends Thread {
	private Socket s = null;
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;

	private Connection con = null;
	private String userName = null;

	public ServerThread(Socket s, Connection con) {
		super("ServerThread");
		this.s = s;
		this.con = con;

		try {
			this.objOut = new ObjectOutputStream(this.s.getOutputStream());
			this.objIn = new ObjectInputStream(this.s.getInputStream());
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		System.out.println(s.getInetAddress().getHostAddress() + ":connected");

		try {
			if(this.authenticate()) {
				Message command = null;

				boolean run = true;
				while(run) {
					command = (Message)this.objIn.readObject();
					int type = command.getMessageType();

					switch(type) {
					case Message.TERMINATE:
						run = false;
						break;
					case Message.ACCT_MSG:
						this.getAccountInfo();
						break;
					case Message.TRANSACTION_MSG:
						this.getTransactionInfo();
						break;
					case Message.CHG_PWD_MSG:
						this.changePassword();
						break;
					case Message.TRANSFER_MSG:
						this.createTransfer();
						break;
					default:
						this.unknownCommand();
					}
				}
			}
		}
		catch(SQLException ex) {
			this.handleSQLExc(ex);
			ex.printStackTrace();
		}
		catch(EOFException ex) {
			//Client terminated without sending a message.
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			this.shutdown();
		}
	}

	private boolean
	authenticate() throws IOException, SQLException, ClassNotFoundException {
		boolean ret_val = false;
		String password = null;

		Message m = (Message)this.objIn.readObject();
		if(m.getMessageType() == Message.LOGIN_MSG) {
			Login l = (Login)m.getData();
			this.userName = l.getUsername();
			password = l.getPassword();
			m = null;
		}
		else {
			m = null;
			m = new Message(Message.STRING_MSG, "Authentication Failure");
			this.objOut.writeObject(m);
			this.objOut.flush();
			m = null;
			return false;
		}

		if(this.userName.equals("admin") && password.equals("shutdown")) {
			System.out.println("SHUTTING DOWN");
			this.shutdown();
			this.con.close();
			System.exit(0);
		}

		Statement smt = this.con.createStatement();
		ResultSet rs = smt.executeQuery(
			"SELECT password FROM Users WHERE username=\"" +
			this.userName + "\"");

		if(rs.first()) {
			if(password.equals(rs.getString(1))) {
				m = new Message(Message.BOOL_MSG, true);
				objOut.writeObject(m);
				ret_val = true;
			}
			else {
				m = new Message(Message.BOOL_MSG, false);
				objOut.writeObject(m);
				ret_val = false;
			}
		}

		objOut.flush();

		if(rs != null)
			rs.close();
		if(smt != null)
			smt.close();

		return ret_val;
	}

	private void
	shutdown() {
		try {
			if(this.objOut != null)
				this.objOut.close();

			if(this.objIn != null)
				this.objIn.close();

			System.out.println(s.getInetAddress().getHostAddress()
															+ ":disconnected");
			this.s.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	private void
	handleSQLExc(SQLException ex) {
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	}

	private void
	getAccountInfo() throws IOException, SQLException {
		String query =
			"select A.Account_Number, A.Account_Type, A.current_balance, " +
			"A.Routing_Number, A.Interest_Rate " +
				"from User_Accounts as A " +
				"Left join UserToAccount as UA on A.Account_Number = " +
				"UA.Account_Number Left join Users as U on U.SSN = " +
				"UA.User_SSN where U.username=\"" + this.userName + "\"";

		Statement smt = this.con.createStatement();
		ResultSet rs = smt.executeQuery(query);

		Message m = null;
		int records = rs.last() ? rs.getRow() : 0;
		rs.beforeFirst();

		m = new Message(records, null);
		objOut.writeObject(m);
		m = null;

		while(rs.next()) {
			int an = rs.getInt("Account_Number");
			int type = rs.getInt("Account_Type");
			float b = rs.getFloat("current_balance");
			int rn = rs.getInt("Routing_Number");
			float ir = rs.getFloat("Interest_Rate");
			m = new Message(Message.ACCT_MSG, new account(an, type, b, rn, ir));
			objOut.writeObject(m);
			m = null;
		}

		objOut.flush();

		if(rs != null)
			rs.close();
		if(smt != null)
			smt.close();
	}

	private void
	getTransactionInfo() {

	}

	private void
	changePassword() {

	}

	private void
	createTransfer() {

	}

	private void
	unknownCommand() {

	}
}
