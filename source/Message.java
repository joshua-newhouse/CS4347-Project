import java.io.Serializable;

public class Message implements Serializable {
	public static final int TERMINATE = 0;
	public static final int LOGIN_MSG = 1;
	public static final int ACCT_MSG = 2;
	public static final int STRING_MSG = 3;
	public static final int BOOL_MSG = 4;
	public static final int TRANSACTION_MSG = 5;
	public static final int CHG_PWD_MSG = 6;
	public static final int TRANSFER_MSG = 7;
//	public static final int NUM

	public int messageType = 0;
	public Object data = null;

	public
	Message(int type, Object d) {
		this.messageType = type;
		this.data = d;
	}

	public int
	getMessageType() {
		return this.messageType;
	}

	public Object
	getData() {
		return this.data;
	}
}
