import java.net.*;
import java.io.*;
import java.lang.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ServerThread extends Thread {
	private static final String[] SQL_STMT = {
		//0 Account info SQL
		"select A.Account_Number, A.Account_Type, A.current_balance, " +
			"A.Routing_Number, A.Interest_Rate " +
				"from User_Accounts as A " +
				"Left join UserToAccount as UA on A.Account_Number = " +
				"UA.Account_Number Left join Users as U on U.SSN = " +
				"UA.User_SSN where U.SSN=\"",
		//1 Transaction info SQL
		"select T.Transaction_ID, AT.Account_Number, T.Point_of_sale, " +
			"T.Amount, T.date, AT.Type, AT.State " +
			"from Transaction as T " +
				"left join AcctToTrans as AT on " + 
									"T.Transaction_ID = AT.Transaction_ID " +
        		"left join UserToAccount as UA on " +
									"AT.Account_Number = UA.Account_Number " +
        		"left join Users as U on U.SSN = UA.User_SSN where U.SSN=\"",
		//2 Login SQL
		"SELECT password, SSN FROM Users WHERE username=\"",
		//3 Change password 1               //4 Change password 2
		"update Users set password = \"", "\" where Users.SSN=\"",
		//5 Transfer
		"select current_balance from User_Accounts as A Where Exists " +
			"(select Account_Number from UserToAccount as UA where " +
			"A.Account_Number = UA.Account_Number and UA.Account_Number = %d " +
			"and UA.User_SSN = \"%s\")",
		//6 Transfer
		"select current_balance from User_Accounts where Account_Number = %d",
		//7 Transfer
		"update User_Accounts set current_balance = %f " +
			"where Account_Number = %d",
		//8 Transfer
		"insert into Transaction (Point_of_sale, Amount, date) " +
			"values (\"online\", %f, now())",
		//9 Transfer
		"insert into AcctToTrans " +
			"(Account_Number, Transaction_ID, Type, State) " +
			"values (%d, last_insert_id(), \"%s\", 0)",
	};

	private Socket s = null;
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;

	private Connection con = null;
	private int SSN = 0;

	public
	ServerThread(Socket s, Connection con) {
		super("ServerThread");
		this.s = s;
		this.con = con;

		try {
			this.s.setSoTimeout(60000);
			this.objOut = new ObjectOutputStream(this.s.getOutputStream());
			this.objIn = new ObjectInputStream(this.s.getInputStream());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			this.shutdown();
		}
	}

	public void
	run() {
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
					case Message.TRANSACTION_MSG:
						getQuery(type);
						break;
					case Message.CHG_PWD_MSG:
						this.changePassword((String)command.getData());
						break;
					case Message.TRANSFER_MSG:
						this.createTransfer((Transfer)command.getData());
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
		catch(InvalidClassException ex) {
			//Client sent an invalid message object.  Terminate connection.
			System.out.println(ex.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			this.shutdown();
		}
	}

	private boolean
	authenticate() throws Exception {
		Message out = null;
		boolean ret_val = false;
		String userName = null;
		String password = null;

		Statement smt = null;
		ResultSet rs = null;

		Message in = (Message)this.objIn.readObject();
		if(in.getMessageType() == Message.LOGIN_MSG) {
			Login l = (Login)in.getData();
			userName = l.getUsername();
			password = l.getPassword();

			if(userName.equals("admin") && password.equals("shutdown")) {
				System.out.println("SHUTTING DOWN");
				this.shutdown();
				this.con.close();
				System.out.println("Disconnected from database");
				System.exit(0);
			}

			smt = this.con.createStatement();
			rs = smt.executeQuery(SQL_STMT[2] + userName + "\"");

			if(rs.first() && password.equals(rs.getString("password"))) {
				this.SSN = rs.getInt("SSN");
				out = new Message();
				out.setAuthenticated(true);
				ret_val = true;
			}
		}

		if(out == null) {
			out = new Message();
			out.setAuthenticated(false);
		}

		this.objOut.writeObject(out);
		this.objOut.flush();
		out = null;
		in = null;

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
	getQuery(int msgType) throws IOException, SQLException {
		int idx = msgType == Message.TRANSACTION_MSG ? 1 : 0;

		String query = SQL_STMT[idx] + this.SSN + "\"";

		Statement smt = this.con.createStatement();
		ResultSet rs = smt.executeQuery(query);

		Message m = null;
		int records = rs.last() ? rs.getRow() : 0;
		rs.beforeFirst();

		m = new Message(records, null);
		objOut.writeObject(m);
		m = null;

		while(rs.next()) {
			m = new Message(msgType, null);
			m.parse(rs);
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
	changePassword(String newPW) throws Exception {
		String update = SQL_STMT[3] + newPW + SQL_STMT[4] + this.SSN + "\"";
		Statement smt = this.con.createStatement();

		Message m = new Message();
		try {
			smt.executeUpdate(update);
			m.setAuthenticated(true);
		}
		catch(SQLException ex) {
			handleSQLExc(ex);
			m.setAuthenticated(false);
		}
		finally {
			objOut.writeObject(m);
			objOut.flush();
			m = null;
		}

		if(smt != null)
			smt.close();
	}

	private void
	createTransfer(Transfer tx) throws Exception {
		this.con.setAutoCommit(false);

		float fromBalance = 0.0f;
		float toBalance = 0.0f;

		int fromAct = tx.getAcctNumber(Transfer.FROM_ACCT);
		int toAct = tx.getAcctNumber(Transfer.TO_ACCT);
		float amt = tx.getAmount();

		Statement smt = null;
		Message m = new Message(Message.STRING_MSG);
		m.setAuthenticated(true);
		try {
			String query = String.format(SQL_STMT[5], fromAct, this.SSN);
			smt = this.con.createStatement();
			ResultSet rs = smt.executeQuery(query);

			if(rs.next())
				fromBalance = rs.getFloat("current_balance");
			else
				throw new
					FailException("No account number associated with user");

			query = String.format(SQL_STMT[6], toAct);
			rs = smt.executeQuery(query);

			if(rs.next())
				toBalance = rs.getFloat("current_balance");
			else
				throw new
					FailException("No account to transfer to");

			fromBalance -= amt;
			toBalance += amt;

			if(fromBalance < 0.0f)
				throw new
					FailException("Insufficient funds for transfer");

			//Update from balance
			query = String.format(SQL_STMT[7], fromBalance, fromAct);
			smt.executeUpdate(query);

			//Update to balance
			query = String.format(SQL_STMT[7], toBalance, toAct);
			smt.executeUpdate(query);

			//Insert new transaction
			query = String.format(SQL_STMT[8], amt);
			smt.executeUpdate(query);

			//Insert association between transaction and from account
			query = String.format(SQL_STMT[9], fromAct, "debit");
			smt.executeUpdate(query);

			//Insert association between transaction and to account
			query = String.format(SQL_STMT[9], toAct, "credit");
			smt.executeUpdate(query);

			this.con.commit();
		}
		catch(SQLException ex) {
			m.setData("Server Error");
			m.setAuthenticated(false);
			ex.printStackTrace();
			this.con.rollback();
		}
		catch(FailException ex) {
			m.setData(ex.toString());
			m.setAuthenticated(false);
		}
		finally {
			if(smt != null)
				smt.close();

			objOut.writeObject(m);
			objOut.flush();
			m = null;

			this.con.setAutoCommit(true);
		}
	}

	private void
	unknownCommand() {
		//Do nothing
	}
}
