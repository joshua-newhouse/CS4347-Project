CC = javac

all : server client messages

server : server.java ServerThread.java messages
	$(CC) server.java ServerThread.java

client : client.java messages
	$(CC) client.java

messages : Message account Login Transaction

Message : Message.java
	$(CC) Message.java

account : account.java DisplayType
	$(CC) account.java

Login : Login.java
	$(CC) Login.java

Transaction : Transaction.java DisplayType
	$(CC) Transaction.java

DisplayType : DisplayType.java
	$(CC) DisplayType.java

clean :
	rm -f *.class
