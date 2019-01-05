package gui.view;


import java.util.ArrayList;

import gui.model.DIFFICULTY;
import gui.model.SudokuButton;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class GameView {
	
	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	
	private Stage gameStage;
	private Scene gameScene;
	private AnchorPane gamePane;
	
	private Stage menuStage;
	
	private DIFFICULTY difficulty;
	
	private ArrayList<SudokuButton> gameButtons;
	
	public GameView(Stage menu, DIFFICULTY difficulty) 
	{
		menuStage = menu;
		this.difficulty = difficulty;
		
		gameButtons = new ArrayList<SudokuButton>();
		gamePane = new AnchorPane();
		gameScene = new Scene(gamePane, WIDTH, HEIGHT);
		gameStage = new Stage();
		gameStage.setScene(gameScene);
		
		createGameBackground();
		createGameButtons();
		createGameGrid();
		
		menuStage.hide();
		gameStage.show();
		
	}
	
	
	
	private void createGameButtons() 
	{
		SudokuButton backBtn = new SudokuButton("< back");
		backBtn.setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY))
					gameStage.close();
					menuStage.show();
				
			}
			
		});
		backBtn.setLayoutX(20);
		backBtn.setLayoutY(20);
		gameButtons.add(backBtn);
				
		SudokuButton undoBtn = new SudokuButton("undo");
		undoBtn.setLayoutX(200);
		undoBtn.setLayoutY(60);		
		gameButtons.add(undoBtn);
		
		SudokuButton redoBtn = new SudokuButton("redo");
		redoBtn.setLayoutX(350);
		redoBtn.setLayoutY(60);		
		gameButtons.add(redoBtn);		

		SudokuButton infoBtn = new SudokuButton("info");
		infoBtn.setLayoutX(590);
		infoBtn.setLayoutY(20);		
		gameButtons.add(infoBtn);

		SudokuButton newGameBtn = new SudokuButton("new game");
		newGameBtn.setLayoutX(590);
		newGameBtn.setLayoutY(120);		
		gameButtons.add(newGameBtn);

		SudokuButton restartBtn = new SudokuButton("restart");
		restartBtn.setLayoutX(590);
		restartBtn.setLayoutY(170);		
		gameButtons.add(restartBtn);		

		SudokuButton hintBtn = new SudokuButton("hint");
		hintBtn.setLayoutX(590);
		hintBtn.setLayoutY(220);		
		gameButtons.add(hintBtn);		
		
		SudokuButton deleteBtn = new SudokuButton("delete");
		deleteBtn.setLayoutX(590);
		deleteBtn.setLayoutY(480);		
		gameButtons.add(deleteBtn);
				
		SudokuButton stopBtn = new SudokuButton("stop");
		stopBtn.setLayoutX(200);
		stopBtn.setLayoutY(530);		
		gameButtons.add(stopBtn);	

		SudokuButton playBtn = new SudokuButton("play");
		playBtn.setLayoutX(350);
		playBtn.setLayoutY(530);		
		gameButtons.add(playBtn);

		SudokuButton saveBtn = new SudokuButton("save");
		saveBtn.setLayoutX(590);
		saveBtn.setLayoutY(530);		
		gameButtons.add(saveBtn);
		
		for(SudokuButton gb : gameButtons) {
			gb.setScaleX(0.7);
			gb.setScaleY(0.7);
			gamePane.getChildren().add(gb);
		}

		int pos = 170;
		for(int i = 1; i < 10; i++) {
			NumberButton b = new NumberButton(Integer.toString(i));
			b.setLayoutX(pos);
			b.setLayoutY(492);
			pos += 47;
			gamePane.getChildren().add(b);
		}
	}



	private void createGameGrid() 
	{
		int x;
		int y = 130;
		
		for(int r = 1; r <= 9; r++) {
			x = 190;
			for (int c = 1; c <= 9; c++) {
				SudokuCell cell = new SudokuCell(r,c,0);
				cell.setLayoutX(x);
				if(c == 3 || c == 6)
					x += 50;
				else
					x += 40;
				cell.setLayoutY(y);
				gamePane.getChildren().add(cell);
			}
			if(r == 3 || r == 6)
				y += 45;
			else
				y += 38;
		}
	}



	private void createGameBackground() 
	{
		Image backroundImage = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backroundImage, BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		gamePane.setBackground(new Background(background));
		
		Rectangle rectangle = new Rectangle(160, 115, 420, 411);
		rectangle.setFill(Color.rgb(70, 169, 255));
		rectangle.setStroke(Color.rgb(90, 125, 155));
		Rectangle rectangle2 = new Rectangle(200, 145, 335, 320);
		rectangle2.setFill(Color.rgb(90, 125, 155));

		gamePane.getChildren().add(rectangle);
		gamePane.getChildren().add(rectangle2);
	}

	public Stage getStage() { return gameStage; }

}
