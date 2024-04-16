import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server {


	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<String> usernames = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;

	// Default Constructor
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}


	/*
	Server Thread Class
	 */
	public class TheServer extends Thread {

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");

				while(true) {
					ClientThread c = new ClientThread(mysocket.accept());
					clients.add(c);
					c.start();
			    }
			} catch (Exception e) {}
		}
	}
	

	/*
	Client's Server-side Thread Class
	 */
	class ClientThread extends Thread {

		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;
		String clientName;

		// Default Constructor
		ClientThread(Socket s){this.connection = s;}


		// ----------------------------- Run Starts Below -----------------------------

		public void run() {

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			while(true) {
				try {

					Message msg = (Message) in.readObject();

					// Input message requesting to check for unique name
					if (msg.flagIsCheckUniqueName()) {
						checkUniqueUsername(msg.getPlayerName());
					}
					// Input message requesting to add new user to 'usernames'
					else if (msg.flagIsNewClientJoined()) {
						clientName = msg.getPlayerName();
						usernames.add(msg.getPlayerName());
					}


				}
				// Client got disconnected
				catch(Exception e) {

					String callBackMsg = "-- " + clientName + " has left the server! --";
					// Send "left the server" notification to GUI
					callback.accept(callBackMsg);
					// Update array of client threads, 'clients' and users 'usernames'
					usernames.remove(clientName);
					clients.remove(this);
					break;
				}
			}
		}




		// ----------------------------- Helper Functions Below -----------------------------

		/*
		Send 'Message' to current client thread
		 */
		public void send(Message msg) {

			try {out.writeObject(msg);}
			catch (IOException e){e.printStackTrace();};
		}

		/*
		Check for unique username
		 */
		public void checkUniqueUsername(String username) {
			// Loop through all the usernames in 'usernames'
			for (int i = 0; i < usernames.size(); i++) {
				// Found existing username, send false message to client
				if(Objects.equals(username, usernames.get(i))) {
					send(new Message(username, "false", "flagIsCheckUniqueName"));
					return;
				}
			}
			// Username is unique, send true message to client
			send(new Message(username, "true", "flagIsCheckUniqueName"));
		}


	}
}


	
	

	
