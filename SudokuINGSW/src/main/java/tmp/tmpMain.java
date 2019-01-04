package tmp;

import javafx.application.Application;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class tmpMain  extends Application {

	@Override
	public void start(Stage primaryStage) 
	{
		try {
			
			GameView manager = new GameView();
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
