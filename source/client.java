import java.util.Scanner;
import java.net.*;
import java.io.*;

public class client {
//	static final String HOST = "localhost";
	static final String HOST =
							"ec2-34-210-134-59.us-west-2.compute.amazonaws.com";
	static final int PORT = 10001;

	private static Scanner input = null;
	private	static Socket s = null;
	private static ObjectInputStream objIn = null;
	private static ObjectOutputStream objOut = null;
	private static String uname = null;

	public static void main(String[] args) {
		input = new Scanner(System.in);
		System.out.print("Enter user name: ");
		uname = input.next();
		System.out.print("Enter user password: ");
		String pw = input.next();

		try {
			s = new Socket(HOST, PORT);
			s.setSoTimeout(5000);

			objIn = new ObjectInputStream(s.getInputStream());
			objOut = new ObjectOutputStream(s.getOutputStream());

			Message m = new Message(Message.LOGIN_MSG, new Login(uname, pw));
			objOut.writeObject(m);
			objOut.flush();
			m = null;

			m = (Message)objIn.readObject();
			if(m.getMessageType() != Message.BOOL_MSG) {
				System.out.println("Login Failed: " + m.getMessageType());
				System.out.println(m.getData().toString());
			}

			if((Boolean)m.getData()) {
				System.out.println("Login successful");
				runMenu();
			}
			else {
				System.out.println("Login failed");
			}

			terminateClient();
		}
		catch(EOFException ex) {
			System.out.println("Connection terminated by host");
			System.exit(1);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static void
	runMenu() throws Exception {
		char selection = '\0';

		while(true) {
			System.out.println("Welcome " + uname);
			System.out.println("Please make a selection:\n");
			System.out.println("1. View accounts information.");
			System.out.println("2. View transactions.");
			System.out.println("3. Change password.");
			System.out.println("4. Create transfer.");
			System.out.println("q. Quit and logout.");

			selection = (char)System.in.read();
			System.in.skip(Long.MAX_VALUE);


			switch(selection) {
			case '1':
				showAccountInfo();
				break;
			case '2':
				break;
			case '3':
				break;
			case '4':
				break;
			case 'q':
				return;
			default:
			}
		}
	}

	private static void
	terminateClient() throws IOException {
		if(objOut != null) {
			Message m = new Message(Message.TERMINATE, null);
			objOut.writeObject(m);
			objOut.flush();
			m = null;
		}

		if(input != null)
			input.close();
		if(objIn != null)
			objIn.close();
		if(objOut != null)
			objOut.close();
		if(s != null)
			s.close();
	}

	private static void
	showAccountInfo() throws Exception {
		Message m = new Message(Message.ACCT_MSG, null);
		objOut.writeObject(m);
		objOut.flush();
		m = null;

		account a = null;
		m = (Message)objIn.readObject();
		int records = m.getMessageType();
		m = null;
		while(records > 0) {
			m = (Message)objIn.readObject();
			a = (account)m.getData();
			a.display();
			System.out.println("*************************\n");
			a = null;
			m = null;
			records--;
		}
	}
}
