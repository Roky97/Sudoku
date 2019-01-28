package gui.view;

import java.util.ArrayList;

import gui.model.*;

import javafx.stage.Stage;
import logic.ai.GameManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.VBox;


@SuppressWarnings("restriction")
public class MenuView extends ViewManager implements IView {

	private Stage stage;
	
	private static final int MENU_BUTTON_X = 50;
	private static final int MENU_BUTTON_Y = 150;
	
	private ArrayList<SudokuButton> menuButtons; 
	
	private SudokuSubScene difficultySubScene;
	private SudokuSubScene scannerSubScene;
	
	public MenuView() 
	{
		stage = new Stage();
		
		setMenuButtons(new ArrayList<SudokuButton>());

		createBackground();
		createSubScenes();
		createButtons();
		
		stage.setScene(scene);
	}
	
	public void createBackground() 
	{
		Image backgroundImg = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backgroundImg, BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		pane.setBackground(new Background(background));
		
		ImageView logo = new ImageView("gui/resources/sudokuLogo.png");
		logo.setLayoutX(WIDTH/2 - 100);
		logo.setLayoutY(150);
		logo.setScaleX(0.9);
		logo.setScaleY(0.9);
		pane.getChildren().add(logo);
	}
	
	public void createButtons() 
	{
		SudokuButton playBtn = new SudokuButton("PLAY");
		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				difficultySubScene.moveSubScene();
			}
		});
		menuButtons.add(playBtn);
		SudokuButton loadBtn = new SudokuButton("LOAD GAME");
		loadBtn.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				if(gameManager.loadGame()) 
				{
					GameView game = new GameView(gameManager.getSudokuCells());
					game.setDifficulty(gameManager.getDifficulty());
					game.hideStage(stage);
				}
			}
		});
		menuButtons.add(loadBtn);
		SudokuButton scannerBtn = new SudokuButton("SCANNER");
		scannerBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) 
			{
				scannerSubScene.moveSubScene();
			}
		});
		menuButtons.add(scannerBtn);
		SudokuButton exitBtn = new SudokuButton("EXIT");
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		menuButtons.add(exitBtn);
		addButtons();
	}

	private void addButtons() 
	{
		for(SudokuButton button : menuButtons) 
		{
			button.setLayoutX(MENU_BUTTON_X);
			button.setLayoutY(MENU_BUTTON_Y + menuButtons.indexOf(button) * 100);
			pane.getChildren().add(button);
		}
	}

	private void createSubScenes() 
	{
		difficultySubScene = new SudokuSubScene();
		difficultySubScene.setLabel("CHOOSE DIFFICULTY");
		difficultySubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		
		ArrayList<SudokuButton> buttons = new ArrayList<SudokuButton>();
		final SudokuButton easy = new SudokuButton("EASY");
		easy.setDifficulty(DIFFICULTY.EASY);
		easy.setDifficultyStyle();
		easy.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) {
				playSudoku(easy);
			}

		});
		buttons.add(easy);
		final SudokuButton medium = new SudokuButton("MEDIUM");
		medium.setDifficulty(DIFFICULTY.NORMAL);
		medium.setDifficultyStyle();
		medium.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) {
				playSudoku(medium);
			}
		});
		buttons.add(medium);
		final SudokuButton hard = new SudokuButton("HARD");
		hard.setDifficulty(DIFFICULTY.HARD);
		hard.setDifficultyStyle();
		hard.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) {
				playSudoku(hard);
			}
		});
		buttons.add(hard);
		
		difficultySubScene.addButtons(buttons);
		
		pane.getChildren().add(difficultySubScene);
		
		VBox buttonsBox = new VBox();
		buttonsBox.setSpacing(20);
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox.getChildren().addAll(difficultySubScene.getButtons());
		buttonsBox.setLayoutX(130);
		buttonsBox.setLayoutY(100);
		
		difficultySubScene.getPane().getChildren().add(difficultySubScene.getLabel());
		difficultySubScene.getPane().getChildren().add(buttonsBox);
		
		//scanner
		scannerSubScene = new SudokuSubScene();
		scannerSubScene.setLabel("SCANSIONE SUDOKU DA IMMAGINE");
		scannerSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		
		ArrayList<SudokuButton> buttons2 = new ArrayList<SudokuButton>();
		SudokuButton cameraBtn = new SudokuButton("CAMERA");
		cameraBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				//azioni per scansione da camera
			}
		});
		buttons2.add(cameraBtn);
		SudokuButton galleryBtn = new SudokuButton("GALLERIA");
		galleryBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				
			}
		});
		buttons2.add(galleryBtn);
		scannerSubScene.addButtons(buttons2);
		
		VBox buttonsBox2 = new VBox();
		buttonsBox2.setSpacing(20);
		buttonsBox2.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox2.getChildren().addAll(scannerSubScene.getButtons());
		buttonsBox2.setLayoutX(130);
		buttonsBox2.setLayoutY(130);
		scannerSubScene.setLayout(30,30);
		scannerSubScene.getPane().getChildren().add(scannerSubScene.getLabel());
		scannerSubScene.getPane().getChildren().add(buttonsBox2);
		
		pane.getChildren().add(scannerSubScene);
	}

	private void playSudoku(SudokuButton button) 
	{
		GameView game = new GameView(button.getDifficulty());
		game.hideStage(stage);
	}
	
	public Stage getStage() { return stage; }

	public ArrayList<SudokuButton> getMenuButtons() {
		return menuButtons;
	}

	public void setMenuButtons(ArrayList<SudokuButton> menuButtons) {
		this.menuButtons = menuButtons;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

}
