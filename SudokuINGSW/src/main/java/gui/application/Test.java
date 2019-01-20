package gui.application;

import gui.view.IView;
import gui.view.MenuView;
import javafx.application.Application;
import javafx.stage.Stage;
import logic.ai.GameManager;

@SuppressWarnings("restriction")
public class Test extends Application {

	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
			GameManager gameManager = new GameManager();
			IView view = new MenuView();
			view.setGameManager(gameManager);
			primaryStage = view.getStage();
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
