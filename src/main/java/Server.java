import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server {


	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<String> usernames = new ArrayList<>();
	ArrayList<Game> games = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;
	String str_playerWhoIsWaiting = "-";
	Ships ships_playerWhoIsWaiting;
	HashMap<String, Integer> hashMap_sunDatabase = new HashMap<>();
	ArrayList<String> rankings = new ArrayList<>();


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
		int sun_points;
		ArrayList<Element> array_clientElements;
		Game clientGame;


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

					Object data = in.readObject();
					// Type 'Message' input
					if (data.getClass().toString().equals("class Message")) {

						Message msg = (Message) data;
						// Input message requesting to check for unique name
						if (msg.flagIsCheckUniqueName()) {
							checkUniqueUsername(msg.getPlayerName());
						}
						// Input message requesting to add new user to 'usernames'
						else if (msg.flagIsNewClientJoined()) {
							clientName = msg.getPlayerName();
							sun_points = 0;
							usernames.add(msg.getPlayerName());
							// Update 'rankings' and sun database
							hashMap_sunDatabase.put(clientName, sun_points);
							placeNameInRanking(clientName);
							// Update clients with updated rankings
							updateAllClientsRankings();
						}
						// Input message is sending points information
						else if (msg.flagIsSendPoints()) {
							// Update 'rankings' and sun database
							sun_points = msg.getSunPoints();
							hashMap_sunDatabase.put(clientName, sun_points);
							placeNameInRanking(clientName);
							// Update clients with updated rankings
							updateAllClientsRankings();
						}
					}
					// Type 'Element' input
					else if (data.getClass().toString().equals("class Element")) {

						Element elem = (Element) data;
						clientGame.playerMove(clientName, elem);
					}
					// Type 'Ships' input
					else if (data.getClass().toString().equals("class Ships")) {

						Ships ships = (Ships) data;
						// New player passing in their new ships grid
						if (!ships.inGame) {
							// New player requesting PvP
							if (Objects.equals(ships.opponentName, "Player")) {
								// No player is in queue
								if (Objects.equals(str_playerWhoIsWaiting, "-")) {
									str_playerWhoIsWaiting = clientName;
									ships_playerWhoIsWaiting = ships;
								}
								// Player is in queue, start game
								else {
									clientGame = new Game(ships, ships_playerWhoIsWaiting);
									for (int i = 0; i < clients.size(); i++) {
										if (Objects.equals(clients.get(i).clientName,
												str_playerWhoIsWaiting)) {
											clients.get(i).clientGame = clientGame;
											clientGame.setGameThreads(this, clients.get(i));
										}
									}
									games.add(clientGame);

									clientGame.sendClientsInitialGrid();
									str_playerWhoIsWaiting = "-";
									ships_playerWhoIsWaiting = null;
								}
							}
							// New Player requesting battle with AI
							else if (Objects.equals(ships.opponentName, "AI")) {

								// FIXME -->
								clientGame = new Game(ships, this);
								clientGame.setGameThreads(this, null);
								games.add(clientGame);

							}
						}
					}

				}
				// Client got disconnected
				catch(Exception e) {

					String callBackMsg = "-- " + clientName + " has left the server! --";
					// Send "left the server" notification to GUI
					callback.accept(callBackMsg);

					// Remove current game
					for (int i = 0; i < games.size(); i++) {
						if (Objects.equals(games.get(i).str_player1, clientName)
								|| Objects.equals(games.get(i).str_player2, clientName)) {
							games.get(i).unexpectedExit(clientName);
						}
					}
					games.remove(clientGame);

					// Update array of client threads 'clients' and users 'usernames'
					usernames.remove(clientName);
					clients.remove(this);

					// Remove from database of sun and 'rankings'
					hashMap_sunDatabase.remove(clientName);
					rankings.remove(clientName);
					// Update clients with updated rankings
					updateAllClientsRankings();

					break;
				}
			}
		}


		// ----------------------------- Helper Functions Below -----------------------------

		/*
		Send 'Message' to current client thread
		 */
		public void send(Object msg) {

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

		/*
		Put 'clientName' in correct order in ranking
		 */
		public void placeNameInRanking(String name) {
			if (rankings.isEmpty() || !rankings.contains(name)) {
				rankings.add(name);
			}
			else {
				rankings.remove(name);
				for (int i = 0;  i < rankings.size(); i++) {
					if (hashMap_sunDatabase.get(name)
							>= hashMap_sunDatabase.get(rankings.get(i))) {
						rankings.add(i, name);
						return;
					}
				}
				rankings.add(name);
			}
		}

		/*
		Update all client to be up-to-date with the rankings
		 */
		public void updateAllClientsRankings() {
			for (ClientThread client : clients) {
				client.send(new Message("","clear", "flagIsUpdateRankings"));
				for (int i = 0; i < rankings.size(); i++) {
					client.send(new Message(rankings.get(i),
							String.valueOf(hashMap_sunDatabase.get(rankings.get(i))),
							"flagIsUpdateRankings"));

				}
			}

			System.out.println("UPDTE BELOOW __________>>>>>>>");
			for (int i = 0; i < rankings.size(); i++) {
				System.out.println((i+1) + ". " + rankings.get(i) + ", "
						+ hashMap_sunDatabase.get(rankings.get(i)));
			}
		}

	}









	/*
	Game Class which contains two players which are currently in a game
	 */
	public class Game {
		Ships ships_player1, ships_player2;
		String str_player1, str_player2;
		ClientThread thread_player1, thread_player2;
//		AI gameAI;

		// Default constructor, for PvP
		public Game(Ships ships_player1, Ships ships_player2) {
			this.ships_player1 = ships_player1;
			this.ships_player2 = ships_player2;
			this.str_player1 = ships_player1.playerName;
			this.str_player2 = ships_player2.playerName;

			ships_player1.opponentName = str_player2;
			ships_player2.opponentName = str_player1;
			ships_player1.inGame = true;
			ships_player2.inGame = true;
		}

		// Helper default constructor for setting up threads
		public void setGameThreads(Server.ClientThread t1, Server.ClientThread t2) {
			thread_player1 = t1;
			thread_player2 = t2;
		}

		// Default constructor, for AI
		public Game(Ships ships_player1, Server.ClientThread t1) {
			this.ships_player1 = ships_player1;
//			this.ships_player2 = gameAI.ships();
			thread_player1 = t1;
			ships_player1.opponentName = "ROBO-ZOMBZ";
			ships_player2.opponentName = str_player1;
			ships_player1.inGame = true;
		}





		/*
		   Helper Functions
		 */

		// Send both clients the initial ships
		public void sendClientsInitialGrid() {
			thread_player1.send(ships_player1);
			thread_player1.send(ships_player2);
			thread_player2.send(ships_player1);
			thread_player2.send(ships_player2);
			thread_player1.send(new Message(str_player1, "", "flagIsStartGameYourTurn"));
			thread_player2.send(new Message(str_player2, "", "flagIsStartGameOppTurn"));
		}

		// A player exit unexpectedly, fix
		public void unexpectedExit(String player) {
			if (Objects.equals(player, str_player1)) {
				thread_player2.send(new Message(str_player2, "true", "flagIsClientWon"));
			}
			else if (player == str_player2) {
				thread_player1.send(new Message(str_player1, "true", "flagIsClientWon"));
			}
			ships_player1.inGame = false;
			ships_player2.inGame = false;
		}

		// Player's move
		public void playerMove(String player, Element elem) {
			if (Objects.equals(player, str_player1)) {
				Element retElem = ships_player2.didItHitPlant(elem.getX(), elem.getY());
				// If all of player 2's ships are all hit, send win to player 1 and lose to player 2
				if (ships_player2.isPeaSunk() && ships_player2.isSunSunk() && ships_player2.isWallSunk()
					&& ships_player2.isSnowSunk() && ships_player2.isChompSunk()) {
					thread_player1.send(new Message(str_player1, "true", "flagIsClientWon"));
					thread_player2.send(new Message(str_player2, "true", "flagIsClientLost"));
					ships_player1.inGame = false;
					ships_player2.inGame = false;
					games.remove(this);
					return;
				}
				thread_player2.send(retElem);
			}
			else if (Objects.equals(player, str_player2)) {
				Element retElem = ships_player1.didItHitPlant(elem.getX(), elem.getY());
				// If all of player 1's ships are all hit, send win to player 2 and lose to player 1
				if (ships_player1.isPeaSunk() && ships_player1.isSunSunk() && ships_player1.isWallSunk()
						&& ships_player1.isSnowSunk() && ships_player1.isChompSunk()) {
					thread_player1.send(new Message(str_player1, "true", "flagIsClientLost"));
					thread_player2.send(new Message(str_player2, "true", "flagIsClientWon"));
					ships_player1.inGame = false;
					ships_player2.inGame = false;
					games.remove(this);
					return;
				}
				thread_player1.send(retElem);
			}
		}

		// Send client initial grid, if playing against AI
		public void sendClientAIGrid() {
			thread_player1.send(ships_player2);
		}
	}
}
