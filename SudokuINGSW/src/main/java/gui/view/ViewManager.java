package gui.view;
import gui.model.sudokuButton;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.util.*;
public class ViewManager {
	
	private static final int HEIGHT = 720;
	private static final int WIDTH = 1080;
	
	private Stage mainStage;
	private Scene mainScene;
	private AnchorPane mainPane;
	
//	private sudokuSubScene difficulty; //Da fare!
	
//	private sudokuSubScene moveToHide;
	
	private static final int MENU_BUTTON_START_X = 100;
	private static final int MENU_BUTTON_START_Y = 150;
	
	private ArrayList<sudokuButton> menuButtons;
	
	public ViewManager() {
		menuButtons = new ArrayList<sudokuButton>();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane,WIDTH, HEIGHT);
		mainStage = new Stage();
		mainStage.setScene(mainScene);
		
		createBackground();
		createMenuButtons();
		createLogo();
//		createSubScene();
		
		
	}
	
	private void createMenuButtons() {
		createStartButton();
//		createScoreButton();
//		createHelpButton();
		createExitButton();
		
		
		/*SpaceRunnerButton button = new SpaceRunnerButton("ciao");
		button.setLayoutX(100);
		button.setLayoutY(100);
		mainPane.getChildren().add(button);
		createBackground(); */
	}
	
	private void createExitButton() {
		sudokuButton button = new sudokuButton("EXIT");
		addMenuButton(button);
		
		button.setOnAction(new EventHandler<ActionEvent>() {

			
			public void handle(ActionEvent event) {
				mainStage.close();
				
			}
			
		});
		
		
	}

	private void createStartButton() {
		sudokuButton button = new  sudokuButton("PLAY");
		addMenuButton(button);
		
		button.setOnAction(new EventHandler<ActionEvent>() {

			
			public void handle(ActionEvent event) {
//				showSubScenes(shipChooserSubScene); //DA FARE
				
			}
			
		});
		
	}
	
	private void createLogo() {
		final ImageView logo = new ImageView("gui/resources/sudokuLogo.png");
		logo.setLayoutX(420);
		logo.setLayoutY(150);
		logo.setOnMouseEntered(new EventHandler<Event>() {

			
			public void handle(Event event) {
				logo.setEffect(new DropShadow());
				
			}
			
		});
		
		logo.setOnMouseExited(new EventHandler<Event>() {

			
			public void handle(Event event) {
				logo.setEffect(null);
				
			}
			
		});
		
		mainPane.getChildren().add(logo);
	}
	
	
	private void addMenuButton(sudokuButton button) {
		button.setLayoutX(MENU_BUTTON_START_X);
		button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size() * 100);
		menuButtons.add(button);
		mainPane.getChildren().add(button);
		
		
		
	}
	
	public Stage getStage() {
		return mainStage;
	}
	
	private void createBackground() {
		Image backroundImage = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backroundImage, BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		mainPane.setBackground(new Background(background));
	}
	
	
	

}
