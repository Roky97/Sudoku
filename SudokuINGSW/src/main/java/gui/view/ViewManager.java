package gui.view;

import gui.model.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("restriction")
public class ViewManager {
	
	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	
	private Stage mainStage;
	private Scene mainScene;
	private AnchorPane mainPane;
	
	private SudokuSubscene difficultySubScene; 
	private SudokuSubscene subSceneToHide;
	
	private static final int MENU_BUTTON_START_X = 50;
	private static final int MENU_BUTTON_START_Y = 150;
	
	private ArrayList<SudokuButton> menuButtons;
	
	private DIFFICULTY difficultyChoosen;
	
	public ViewManager() {
		menuButtons = new ArrayList<SudokuButton>();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane,WIDTH, HEIGHT);
		mainStage = new Stage();
		mainStage.setScene(mainScene);
		
		createBackground();
		createMenuButtons();
		createLogo();
		createSubScene();
		
		
	}
	
	private void createSubScene() 
	{
		difficultySubScene = new SudokuSubscene();
		mainPane.getChildren().add(difficultySubScene);
		Label chooseDifficultyLabel = new Label("CHOOSE DIFFICULTY");
		chooseDifficultyLabel.setLayoutX(120);
		chooseDifficultyLabel.setLayoutY(30);
		try {
			chooseDifficultyLabel.setFont(Font.loadFont(new FileInputStream(new File("src/main/java/gui/resources/TeachersStudent.ttf")), 35));
		} catch (FileNotFoundException e) {
			System.err.println("FONT NON TROVATO!");
			chooseDifficultyLabel.setFont(Font.loadFont("Verdana", 23));			
		};
		
		VBox difficutyButtons = new VBox();
		difficutyButtons.setSpacing(20);
		difficutyButtons.setAlignment(Pos.CENTER_RIGHT);
		
		final SudokuDifficultyButton easy = new SudokuDifficultyButton("Facile", DIFFICULTY.EASY);
		final SudokuDifficultyButton normal = new SudokuDifficultyButton("Normale", DIFFICULTY.NORMAL);
		final SudokuDifficultyButton hard = new SudokuDifficultyButton("Difficile", DIFFICULTY.HARD);
		
		easy.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
				{
					difficultyChoosen = easy.getDifficulty();
					GameView gameView = new GameView(mainStage, difficultyChoosen);
				}
			}
		});
		
		normal.setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY))
				{
					difficultyChoosen = normal.getDifficulty();
					GameView gameView = new GameView(mainStage, difficultyChoosen);
				}
				
				
			}
			
		});
		
		hard.setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY))
				{
					difficultyChoosen = hard.getDifficulty();
					GameView gameView = new GameView(mainStage, difficultyChoosen);
				}
				
				
			}
			
		});
		
		difficutyButtons.getChildren().addAll(easy,normal,hard);
		
		difficutyButtons.setLayoutX(130);
		difficutyButtons.setLayoutY(100);
		
		
		difficultySubScene.getPane().getChildren().add(chooseDifficultyLabel);
		difficultySubScene.getPane().getChildren().add(difficutyButtons);
//		difficultySubScene.getPane().getChildren().add(buttonToPlay);
		
	}

	private void createMenuButtons() {
		createStartButton();
//		createScoreButton();
//		createHelpButton();
		createExitButton();
	
	}
	
	private void createExitButton() {
		SudokuButton button = new SudokuButton("EXIT");
		addMenuButton(button);
		
		button.setOnAction(new EventHandler<ActionEvent>() {

			
			public void handle(ActionEvent event) {
				mainStage.close();
				
			}
			
		});
		
		
	}

	private void createStartButton() {
		SudokuButton button = new  SudokuButton("PLAY");
		addMenuButton(button);
		
		button.setOnAction(new EventHandler<ActionEvent>() {

			
			public void handle(ActionEvent event) {
				showSubScenes(difficultySubScene);
				
			}
			
		});
		
	}
	
	protected void showSubScenes(SudokuSubscene moveScene) {
		if(subSceneToHide!=null)
		{
			subSceneToHide.moveSubScene();
		}
		if(subSceneToHide!=moveScene)
			moveScene.moveSubScene();
		subSceneToHide = moveScene;
		
	}

	private void createLogo() {
		final ImageView logo = new ImageView("gui/resources/sudokuLogo.png");
		logo.setLayoutX(WIDTH/2 - 100);
		logo.setLayoutY(150);
		logo.setScaleX(0.9);
		logo.setScaleY(0.9);
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
	
	
	private void addMenuButton(SudokuButton button) {
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
