import java.awt.image.AreaAveragingScaleFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
			}
		}
	}
	

	/*
	Client's Server-side Thread Class
	 */
	class ClientThread extends Thread {

		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;
		String clientThreadName;

		// Default Constructor
		ClientThread(Socket s){this.connection = s;}



		// ----------------------------- Helper Functions Below -----------------------------

		/*
		Update All Clients with 'message'
		 */
		public void updateClients(String message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(message);
				} catch(Exception e) {}
			}
		}

		/*
		Check for unique username
		 */
		public void checkUniqueUsername(String username) {

		}






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

					String data = in.readObject().toString();
					callback.accept("client: " + " sent: " + data);
					updateClients("client #"+" said: "+data);

				} catch(Exception e) {

					String callBackMsg = "-- " + clientThreadName + " has left the server! --";
					// Send "left the server" notification to GUI
					callback.accept(callBackMsg);
					// Send "left the server" notification to all clients
					updateClients(callBackMsg);

					clients.remove(this);
					break;
				}
			}
		}
	}
}


	
	

	
