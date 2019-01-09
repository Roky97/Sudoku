package gui.application;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.view.ViewManager;

@SuppressWarnings("restriction")
public class main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			ViewManager manager = new ViewManager();
			primaryStage = manager.getStage();
		
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
