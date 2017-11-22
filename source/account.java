import java.io.Serializable;

public class account implements Serializable {
	private static String[] accountTypes = {
		"Savings", "Checking"
	};

	private int accountNumber = 0;
	private String accountType = null;
	private float balance = 0.0f;
	private int routingNumber = 0;
	private float interestRate = 0.0f;

	public account(int an, int at, float b, int rn, float ir) {
		this.accountNumber = an;
		this.accountType = accountTypes[at - 1];
		this.balance = b;
		this.routingNumber = rn;
		this.interestRate = ir;
	}

	public void
	display() {
		System.out.println("Account Number: " + this.accountNumber);
		System.out.println("Account Type: " + this.accountType);
		System.out.println("Current Balance: " + this.balance);
		System.out.println("Routing Number: " + this.routingNumber);
		System.out.println("Interest Rate: " + this.interestRate);
	}
}
