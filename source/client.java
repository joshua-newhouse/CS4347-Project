import java.util.Scanner;
import java.net.*;
import java.io.*;

public class client {
	static final String HOST = "localhost";
//	static final String HOST =
//							"ec2-34-210-134-59.us-west-2.compute.amazonaws.com";
	static final int PORT = 10001;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);

		System.out.print("Enter user name: ");
		String uname = input.next();

		System.out.print("Enter user password: ");
		String pw = input.next();

		Socket s = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			s = new Socket(HOST, PORT);
			out = new PrintWriter(s.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
			System.exit(1);
		}

		out.println(uname);
		out.println(pw);

		String fromServer;
		try {
			fromServer = in.readLine();

			if(fromServer.equals("1")) {
				System.out.println("Authentication successful");
				//Call menu procedure
			}
			else if(fromServer.equals("0")){
				System.out.println("Authentication failed");
				System.exit(1);
			}
			else {
				System.out.println("Cannot communicate with server:");
				System.out.println("Message from server: " + fromServer);
				System.exit(1);
			}
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}
}
