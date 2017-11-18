CC = javac

all : server client

server : server.java ServerThread.java
	$(CC) server.java ServerThread.java

client : client.java
	$(CC) client.java

clean :
	rm -f *.class
