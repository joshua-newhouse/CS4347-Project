import java.io.Serializable;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Transaction implements Serializable, DisplayType {
	private static String[] transStates = {
		"Posted", "Pending"
	};

	private int transactionID = 0;
	private int accountNumber = 0;
	private String pointOfSale = null;
	private float amount = 0.0f;
	private String type = null;
	private String date = null;
	private String state = null;

	public
	Transaction(int tID, int an, String p, float a, String t, String d,int s) {
		this.transactionID = tID;
		this.accountNumber = an;
		this.pointOfSale = p;
		this.amount = a;
		this.type = t;
		this.date = d;
		this.state = transStates[s];
	}

	public
	Transaction(ResultSet rs) throws SQLException {
		this.transactionID = rs.getInt("Transaction_ID");
		this.accountNumber = rs.getInt("Account_Number");
		this.pointOfSale = rs.getString("Point_of_sale");
		this.amount = rs.getFloat("Amount");
		this.type = rs.getString("Type");
		this.date = rs.getString("date");
		this.state = transStates[rs.getInt("State")];
	}

	public void
	display() {
		System.out.println("Transaction: " + this.transactionID + 
			"   Account Number: " + this.accountNumber +
			"   Point of Sale: " + this.pointOfSale +
			"   Amount: " + this.amount +
			"   Type: " + this.type +
			"   Date: " + this.date +
			"   Status: " + this.state);
	}
}
