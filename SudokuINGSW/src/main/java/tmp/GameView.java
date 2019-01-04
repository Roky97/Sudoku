package tmp;

import java.awt.Paint;
import java.util.ArrayList;
import gui.model.sudokuButton;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
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
	
	private ArrayList<sudokuButton> gameButtons;
	
	public GameView() 
	{
		gameButtons = new ArrayList<sudokuButton>();
		gamePane = new AnchorPane();
		gameScene = new Scene(gamePane, WIDTH, HEIGHT);
		gameStage = new Stage();
		gameStage.setScene(gameScene);
		
		createGameBackground();
		createGameButtons();
		createGameGrid();
	}
	
	
	
	private void createGameButtons() 
	{
		sudokuButton backBtn = new sudokuButton("< back");
		backBtn.setLayoutX(20);
		backBtn.setLayoutY(20);
		gameButtons.add(backBtn);
				
		sudokuButton undoBtn = new sudokuButton("undo");
		undoBtn.setLayoutX(200);
		undoBtn.setLayoutY(60);		
		gameButtons.add(undoBtn);
		
		sudokuButton redoBtn = new sudokuButton("redo");
		redoBtn.setLayoutX(350);
		redoBtn.setLayoutY(60);		
		gameButtons.add(redoBtn);		

		sudokuButton infoBtn = new sudokuButton("info");
		infoBtn.setLayoutX(590);
		infoBtn.setLayoutY(20);		
		gameButtons.add(infoBtn);

		sudokuButton newGameBtn = new sudokuButton("new game");
		newGameBtn.setLayoutX(590);
		newGameBtn.setLayoutY(120);		
		gameButtons.add(newGameBtn);

		sudokuButton restartBtn = new sudokuButton("restart");
		restartBtn.setLayoutX(590);
		restartBtn.setLayoutY(170);		
		gameButtons.add(restartBtn);		

		sudokuButton hintBtn = new sudokuButton("hint");
		hintBtn.setLayoutX(590);
		hintBtn.setLayoutY(220);		
		gameButtons.add(hintBtn);		
		
		sudokuButton deleteBtn = new sudokuButton("delete");
		deleteBtn.setLayoutX(590);
		deleteBtn.setLayoutY(480);		
		gameButtons.add(deleteBtn);
				
		sudokuButton stopBtn = new sudokuButton("stop");
		stopBtn.setLayoutX(200);
		stopBtn.setLayoutY(530);		
		gameButtons.add(stopBtn);	

		sudokuButton playBtn = new sudokuButton("play");
		playBtn.setLayoutX(350);
		playBtn.setLayoutY(530);		
		gameButtons.add(playBtn);

		sudokuButton saveBtn = new sudokuButton("save");
		saveBtn.setLayoutX(590);
		saveBtn.setLayoutY(530);		
		gameButtons.add(saveBtn);
		
		for(sudokuButton gb : gameButtons) {
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
