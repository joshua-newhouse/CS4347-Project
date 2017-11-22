CC = javac

all : server client messages

server : server.java ServerThread.java messages
	$(CC) server.java ServerThread.java

client : client.java messages
	$(CC) client.java

messages : Message account Login

Message : Message.java
	$(CC) Message.java

account : account.java
	$(CC) account.java

Login : Login.java
	$(CC) Login.java

clean :
	rm -f *.class
