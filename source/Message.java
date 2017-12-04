import java.io.Serializable;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Message implements Serializable {
	public static final int TERMINATE = 100;
	public static final int ACCT_MSG = 1;
	public static final int TRANSACTION_MSG = 2;
	public static final int LOGIN_MSG = 3;
	public static final int STRING_MSG = 4;
	public static final int CHG_PWD_MSG = 5;
	public static final int TRANSFER_MSG = 6;

	public int messageType = 0;
	public Object data = null;

	public
	Message() {
		//Default constructor does nothing
	}

	public
	Message(int msgType) {
		this.messageType = msgType;
	}
	
	public
	Message(int msgType, Object d) {
		this.messageType = msgType;
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

	public void
	setData(Object data) {
		this.data = data;
	}

	public void
	setAuthenticated(boolean isAuthenticated) {
		this.messageType = isAuthenticated ? 1 : 0;
	}

	public boolean
	isAuthenticated() {
		return this.messageType == 0 ? false : true;
	}

	public boolean
	parse(ResultSet rs) throws SQLException {
		if(rs == null)
			return false;

		switch(this.messageType) {
		case ACCT_MSG:
			this.data = new account(rs);
			break;
		case TRANSACTION_MSG:
			this.data = new Transaction(rs);
			break;
		default:
			this.data = null;
		}

		return true;
	}

	public void
	display() {
		if(this.data == null)
			return;

		DisplayType dt = (DisplayType)this.data;
		dt.display();
	}
}
