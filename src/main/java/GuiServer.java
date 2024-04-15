import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class GuiServer extends Application{


	Server serverConnection;
	ListView<String> listItems;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Callback.accept ends up here
		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				// FIXME
			});
		});




		// ----------------------------- Element Definitions Below -----------------------------

		listItems = new ListView<String>();






		// Closing Game
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		// Setup initial scene
		primaryStage.setScene(ServerGUI());
		primaryStage.setTitle("This is the Server");
		primaryStage.show();
		
	}




	// ----------------------------- GUI Functions Below -----------------------------




	/*
	Server GUI code
		~ display logs
	 */
	public Scene ServerGUI() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(50));
		pane.setStyle("-fx-background-color: coral");
		
		pane.setCenter(listItems);
		pane.setStyle("-fx-font-family: 'serif'");
		return new Scene(pane, 500, 400);
	}
}
