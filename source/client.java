/* This is the command line client program.  The main purpose if for testing the
   server but it can also suffice as a final client if the GUI client is a no go
   or it can work as a java backend of the GUI client if necessary.

   The command line client depends on:
        client.java
        Message.java
        Login.java
        account.java
        Transaction.java
        DisplayType.java														  

    The client:
        - gets username and password from user via command line.
        - creates a connection to the server via a socket.
        - creates object input and output streams on the socket for comm.
              back and forth with server.
        - sends a login message to the server and receives response.
        - if response is good authentication then it runs the menu, otherwise
            termintates:
            - The menu is an infinite loop that present the user with options.
            - The user selection is read from the command line and the
                appropriate message is sent to the server.
            - The response is read from the server and presented to the user.
            - When the user quits, the client informs the server of termination
                and closes out the streams and socket.
  */

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class client {
	/* Set host and port for communications with the server */
	static final String HOST = "localhost";
//	static final String HOST =
//							"ec2-34-210-134-59.us-west-2.compute.amazonaws.com";
	static final int PORT = 10001;

	private static Scanner input = null;				//For user input
	private	static Socket s = null;						//Comm. buffer w/server
	private static ObjectInputStream objIn = null;		//Inbound comm. stream
	private static ObjectOutputStream objOut = null;	//Outbound comm. stream

	/* Username will persist for the client and be used for all queries. */
	private static String uname = null;

	public static void main(String[] args) {
		/* Get user login information */
		input = new Scanner(System.in);
		System.out.print("Enter user name: ");
		uname = input.next();
		System.out.print("Enter user password: ");
		String pw = input.next();

		try {
			/* Connect to server and set timeout for 5s of waiting for resp.  */
			s = new Socket(HOST, PORT);
			s.setSoTimeout(5000);

			objIn = new ObjectInputStream(s.getInputStream());
			objOut = new ObjectOutputStream(s.getOutputStream());

			/* Send login message to server */
			Message m = new Message(Message.LOGIN_MSG, new Login(uname, pw));
			objOut.writeObject(m);
			objOut.flush();
			m = null;

			/* Read login response from server.  Server response is 0 if login
			   credentials invalid, nonzero otherwise.  If verified then run
			   the menu for the user. 	                                      */
			m = (Message)objIn.readObject();
			if(m.isAuthenticated()) {
				m = null;
				System.out.println("Login successful");
				runMenu();
			}
			else {
				System.out.println("Login failed");
			}

			/* Inform server of disconnect.  Close all streams. */
			terminateClient();
		}
		catch(SocketException | EOFException ex) {
			System.out.println("Connection terminated by host");
			System.exit(1);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/* runMenu: In an infinite loop:
                    - Presents the user with options.
                    - reads user selection.
                    - calls method to perform the selected action.
                    - terminates on user input of 'q'.             */
	private static void
	runMenu() throws Exception {
		char selection = '\0';

		while(true) {
			System.out.println("\nWelcome " + uname);
			System.out.println("Please make a selection:\n");
			System.out.println("1. View accounts information.");
			System.out.println("2. View transactions.");
			System.out.println("3. Change password.");
			System.out.println("4. Create transfer.");
			System.out.println("q. Quit and logout.");

			/* Get next input character and discard anything else */
			selection = (char)System.in.read();
			System.in.skip(Long.MAX_VALUE);

			switch(selection) {
			case '1':
			case '2':
				show(selection);
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

	/* terminateClient:  Sends a termination message to the server and then
		closes streams and finally the socket                                */
	private static void
	terminateClient() throws IOException {
		if(objOut != null) {
			Message m = new Message(Message.TERMINATE);
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

	/* show:
        - Sends a message to the server requesting a DisplayType
        - gets number of records to be returned by server
        - for each record it prints to command line                           */
	private static void
	show(char sel) throws Exception {
		/* Send account request to server */
		int type = sel == '1' ? Message.ACCT_MSG : Message.TRANSACTION_MSG;
		
		Message m = new Message(type);
		objOut.writeObject(m);
		objOut.flush();
		m = null;

		/* Receive message from server of number of records it returned */
		m = (Message)objIn.readObject();
		int records = m.getMessageType();
		m = null;

		/* Read each record from server and write to command line */
		while(records > 0) {
			m = (Message)objIn.readObject();
			DisplayType dt = (DisplayType)m.getData();
			dt.display();
			System.out.println("*************************\n");
			m = null;
			records--;
		}
	}
}
