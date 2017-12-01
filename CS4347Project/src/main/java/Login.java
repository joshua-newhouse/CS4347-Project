import java.io.Serializable;

public class Login implements Serializable {
	private String username = null;
	private String password = null;

	public
	Login(String uName, String pwd) {
		this.username = uName;
		this.password = pwd;
	}

	public String
	getUsername() {
		return this.username;
	}

	public String
	getPassword() {
		return this.password;
	}
}
