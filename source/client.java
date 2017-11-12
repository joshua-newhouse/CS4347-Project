import java.util.Scanner;
import java.net.*;
import java.io.*;

public class client {
	static final String HOST = "localhost";
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
			while((fromServer = in.readLine()) != null)
				System.out.println(fromServer);
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}
}
