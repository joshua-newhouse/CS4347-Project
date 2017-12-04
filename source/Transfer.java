import java.io.Serializable;

public class Transfer implements Serializable {
	public static final int FROM_ACCT = 0;
	public static final int TO_ACCT = 1;

	private int[] account = {0, 0};
	private float amountToTransfer = 0.0f;

	public
	Transfer(int act1, int act2, float amtTx) {
		this.account[FROM_ACCT] = act1;
		this.account[TO_ACCT] = act2;
		this.amountToTransfer = amtTx;
	}

	public int
	getAcctNumber(int fromTo) {
		return this.account[fromTo];
	}

	public float
	getAmount() {
		return this.amountToTransfer;
	}
}
