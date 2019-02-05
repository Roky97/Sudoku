package gui.view;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import gui.model.DIFFICULTY;
import gui.model.NumberButton;
import gui.model.SudokuButton;
import gui.model.SudokuCell;
import gui.model.SudokuGrid;
import gui.model.SudokuSubScene;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.Score;
import logic.TimeScored;
import logic.ai.Cell;
import logic.ai.GameManager;

@SuppressWarnings("restriction")
public class GameView extends ViewManager implements IView {
	
	private String TEXT_TIMER_STYLE;

	private Stage stage;
	private ArrayList<SudokuButton> gameButtons;
	private ArrayList<NumberButton> numberButtons;
	private SudokuSubScene newGameSubScene;
	private SudokuSubScene restartSubScene;
	private SudokuSubScene infoSubScene;
	private AnimationTimer animationTimer;
	private Text text;
	private SudokuSubScene highscoreSubScene;
	private SudokuSubScene rankingSubScene;
	private SudokuSubScene insertIdSubScene;
	private boolean isBestScore;
	private String userName;
	
	private DIFFICULTY difficulty;
	private SudokuGrid grid;
	
	public GameView(DIFFICULTY difficulty) 
	{
		this.stage = new Stage();
		this.stage.setResizable(false);
		
		this.difficulty = difficulty;

		this.grid = new SudokuGrid();
		
		this.gameButtons = new ArrayList<SudokuButton>();
		this.numberButtons = new ArrayList<NumberButton>();
		this.gameManager = new GameManager();
		this.gameManager.generateSudoku();//FUNZIONE DI GAMEMANAGER MEDIANTE LA QUALE VIENE GENERATO IL SUDOKU (DLV)
		this.gameManager.setDifficulty(this.difficulty);
		text = new Text (gameManager.getTimerString());
		createBackground();
		createButtons();
		createTimerLabel();//INIZIALIZZA ED IMPOSTA IL LABEL RAPPRESENTANTE IL TIMER
		createGrid(this.gameManager.getGrid());//CREAZIONE DEL SUDOKU CON LISTA DI CELL PASSATE DAL GAMEMANAGER
		createSubScene();
		isBestScore=false;

		this.stage.setScene(scene);
		this.stage.show();
	}
	
	public GameView(ArrayList<SudokuCell> sudokuCells) 	//COSTRUTTORE DA UTILIZZARE PER CARICARE LA PARTITA
	{
		stage = new Stage();
		stage.setResizable(false);
		
		gameManager = new GameManager();

		this.grid = new SudokuGrid(sudokuCells);
		
		this.gameButtons = new ArrayList<SudokuButton>();
		this.numberButtons = new ArrayList<NumberButton>();
		text = new Text ();

		createBackground();
		createButtons();
		if(!needSolution()) {
			loadSavedGame();
		}
		else {
			System.out.println("ELSE");
			if(gameManager.getSolution(gameManager.parseToCell(sudokuCells)))
				createGrid(gameManager.getGrid());
		}
		stage.setScene(scene);
		stage.show();
	}
	
	private void loadSavedGame() 
	{
		gameManager.loadGame();
		
		int x;
		int y = 130;

		//creazione e disposizione delle celle grafiche
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(SudokuCell cell : grid.getCells())
				{
					if(cell.getRow() == r && cell.getColumn() == c)
					{
						cell.setScale(1.2,1.2);
						cell.setLayoutX(x);
							
						if(c == 2 || c == 5)
							x += 50;
						else
							x += 40;
	
						cell.setLayoutY(y);
						if(!cell.isHide()) 
						{
							cell.setStartFont();
							cell.showContent();
							for(NumberButton number : numberButtons)
							{
								if(number.getValue() == cell.getValue())
									number.setCont(number.getCont()-1);
							}
						}else {
							cell.removeContent();
						}
						addCellListener(cell);
					}
				}
			}
			if(r == 2 || r == 5)
				y += 45;
			else
				y += 38;
		}
		for(SudokuCell c : gameManager.getSudokuCells()) 
		{
			if(!c.isHide()) 
			{
				for(SudokuCell sc : grid.getCells()) 
				{
					if(sc.checkPosition(c.getRow(), c.getColumn()) && sc.isHide()) 
					{
						System.out.println(c.toString());
						sc.setAssignedValue(c.getAssignedValue());
					}
				}
			}
		}
		gameManager.setSudokuCells(grid.getCells());
		pane.getChildren().addAll(grid.getCells());
		grid.setState(grid);
		gameManager.getCareTaker().add(grid.getState());
	}

	public void createBackground() 
	{
		Image backroundImage = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backroundImage, BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		pane.setBackground(new Background(background));

		Rectangle rectangle = new Rectangle(160, 115, 420, 411);
		rectangle.setFill(Color.rgb(70, 169, 255));
		rectangle.setStroke(Color.rgb(90, 125, 155));
		Rectangle rectangle2 = new Rectangle(200, 145, 335, 320);
		rectangle2.setFill(Color.rgb(90, 125, 155));

		pane.getChildren().add(rectangle);
		pane.getChildren().add(rectangle2);
	}
	
	public void createButtons() 
	{
		SudokuButton backBtn = new SudokuButton("BACK");
		backBtn.setLayoutX(20);
		backBtn.setLayoutY(20);
		gameButtons.add(backBtn);
		backBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					hiddenStage.show();
					stage.close();
					animationTimer.stop();
					gameManager.restartTimer();
				}
			}
			
		});
				
		SudokuButton undoBtn = new SudokuButton("undo");
		undoBtn.setLayoutX(200);
		undoBtn.setLayoutY(60);
		undoBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				removeHighlight();
				if(gameManager.undoIteration(gameManager.getIteration()-1)) {
					grid.setCells(gameManager.getCareTaker().get(gameManager.getIteration()).getCells());
					updateGrid();
				}
			}
		});
		gameButtons.add(undoBtn);
		
		SudokuButton redoBtn = new SudokuButton("redo");
		redoBtn.setLayoutX(350);
		redoBtn.setLayoutY(60);	
		redoBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(gameManager.redoIteration(gameManager.getIteration()+1)) {
					grid.setCells(gameManager.getCareTaker().get(gameManager.getIteration()).getCells());
					updateGrid();
				}
			}
		});
		gameButtons.add(redoBtn);		
		
		SudokuButton infoBtn = new SudokuButton("INFO");
		infoBtn.setLayoutX(590);
		infoBtn.setLayoutY(20);	
		infoBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				for(SudokuButton b : gameButtons) {
					b.setDisable(true);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isDisable())
						n.setDisable(true);
				}
				infoSubScene.moveSubScene();
				gameManager.stopTimer();
				animationTimer.stop();
			}
		});
		gameButtons.add(infoBtn);

		final SudokuButton newGameBtn = new SudokuButton("NEW GAME");
		newGameBtn.setLayoutX(590);
		newGameBtn.setLayoutY(120);		
		gameButtons.add(newGameBtn);
		newGameBtn.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					newGameBtn.setLayoutY(123.0 );
					newGameSubScene.moveSubScene();
					for(SudokuCell c : grid.getCells())
						c.setDisable(!c.isDisable());
					for(SudokuButton b : gameButtons) {
						b.setDisable(!b.isDisable());
					}
					for(NumberButton n : numberButtons) {
						if(!n.isEmpty())
							n.setDisable(!n.isDisable());
					}
					gameManager.stopTimer();
					animationTimer.stop();
				}
			}
		});
		
		final SudokuButton restartBtn = new SudokuButton("RESTART");
		restartBtn.setLayoutX(590);
		restartBtn.setLayoutY(170);		
		gameButtons.add(restartBtn);
		restartBtn.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					restartBtn.setLayoutY(173.0);
					restartSubScene.moveSubScene();
					for(SudokuCell c : grid.getCells())
						c.setDisable(!c.isDisable());
					for(SudokuButton b : gameButtons) {
						b.setDisable(!b.isDisable());
					}
					for(NumberButton n : numberButtons) {
						if(!n.isEmpty())
							n.setDisable(!n.isDisable());
					}
					gameManager.stopTimer();
					animationTimer.stop();
				}
			}
		});

		SudokuButton hintBtn = new SudokuButton("HINT");
		hintBtn.setLayoutX(590);
		hintBtn.setLayoutY(220);		
		gameButtons.add(hintBtn);	
		hintBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				removeHighlight();
				showValue(gameManager.getSelectedCell());
				gameManager.setValue(0);
				gameManager.getCellWithSameValue().clear();
			}
		});
		
		SudokuButton deleteBtn = new SudokuButton("DELETE");
		deleteBtn.setLayoutX(590);
		deleteBtn.setLayoutY(480);		
		gameButtons.add(deleteBtn);
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				int var = gameManager.getSelectedCell().getAssignedValue();
				removeHighlight();
				gameManager.getCellWithSameValue().clear();
				gameManager.setValue(0);
				if(gameManager.removeContent(gameManager.getSelectedCell())) 
				{
					//**********************/GESTIONE MEMENTO/******************************************/
					grid.setState(grid);
					gameManager.increaseIteration();
					gameManager.getCareTaker().add(grid.getState());
					
					for(NumberButton number : numberButtons) {
						if(number.getValue() == var) {
							number.setCont(number.getCont()+1);
							return;
						}
					}
				}
			}
		});
				
		final SudokuButton stopBtn = new SudokuButton("STOP");
		stopBtn.setLayoutX(200);
		stopBtn.setLayoutY(530);		
		gameButtons.add(stopBtn);	
		stopBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					stopBtn.setLayoutY(533.0);
					gameManager.stopTimer();
					animationTimer.stop();
				}
			}
		});

		final SudokuButton playBtn = new SudokuButton("PLAY");
		playBtn.setLayoutX(350);
		playBtn.setLayoutY(530);		
		gameButtons.add(playBtn);
		playBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					playBtn.setLayoutY(533.0);
					gameManager.startTimer();
					animationTimer.start();
				}
			}
		});

		final SudokuButton saveBtn = new SudokuButton("SAVE");
		saveBtn.setLayoutX(590);
		saveBtn.setLayoutY(530);		
		gameButtons.add(saveBtn);
		saveBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					saveBtn.setLayoutY(533.0);
					System.out.println("SAVE");
					gameManager.saveGame(difficulty, grid.getCells());
					saveNotification();
					animationTimer.stop();
					gameManager.restartTimer();
				}
			}
		});

		for(SudokuButton gb : gameButtons) {
			gb.setScaleX(0.7);
			gb.setScaleY(0.7);
			pane.getChildren().add(gb);
		}
		
		int pos = 170;
		for(int i = 1; i < 10; i++) 
		{
			final NumberButton numBtn = new NumberButton(i);
			numBtn.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					removeHighlight();
					if(!numBtn.isEmpty()) {
						gameManager.selectedValue(numBtn.getValue());
					}
					else {
						gameManager.setValue(0);
					}
					
				}
			});
			numBtn.setLayoutX(pos);
			numBtn.setLayoutY(492);
			pos += 47;
			numberButtons.add(numBtn);
		}
		pane.getChildren().addAll(numberButtons);
	}

	public void createTimerLabel() 
	{
		text = new Text(gameManager.getTimerString());

		TEXT_TIMER_STYLE = "-fx-font: 32px Tahoma;"+
							"-fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);"+
							"-fx-stroke: black;"+
							"-fx-stroke-width: 1;";
		text.setStyle(TEXT_TIMER_STYLE);
	    text.setLayoutX(306);
	    text.setLayoutY(50);
		animationTimer = new AnimationTimer() { //AGGIORNA CONTINUAMENTE IL TESTO DEL LABEL BASANDOSI SUL TIMER PRESENTE NEL GAMEMANAGER.
			@Override
			public void handle(long now) 
			{
				gameManager.upgradeTimer();
				String t = gameManager.getTimerString();
				text.setText(t);
			}
		};
		pane.getChildren().add(text);
	}
	
	private boolean needSolution() 
	{
		if(grid.size() != 81)
			return true;
		return false;
	}
	
	protected void updateGrid() 
	{
		ArrayList<SudokuCell> cellToErase = new ArrayList<>();
		for(Object o : pane.getChildren()) {
			if(o instanceof SudokuCell)
				cellToErase.add((SudokuCell) o);
		}
		pane.getChildren().removeAll(cellToErase);
		
		int x;
		int y = 130;

		//creazione e disposizione grafica delle celle
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(SudokuCell cell : grid.getCells()) 
				{
					if(cell.checkPosition(r,c)) 
					{
						cell.setLayout(x,y);
						addCellListener(cell);
						for(SudokuCell startCell : gameManager.getStartGrid()) 
						{
							if(startCell.checkPosition(cell.getRow(), cell.getColumn()) && !startCell.isHide()) {
								cell.setStartFont();
								break;
							}
						}
						break;
					}
				}
				if(c == 2 || c == 5)
					x += 50;
				else
					x += 40;
			}
			if(r == 2 || r == 5)
				y += 45;
			else
				y += 38;
		}
		gameManager.setSudokuCells(grid.getCells());
		pane.getChildren().addAll(grid.getCells());
		
		for(NumberButton n: numberButtons) {
			n.setCont(9);
			for(SudokuCell c : grid.getCells()) {
				if(c.getAssignedValue() == n.getValue()) {
					n.setCont(n.getCont()-1);
				}
			}
		}
		createSubScene();
	}

	public void createSubScene() 
	{
		if(difficulty == null)
			difficulty = DIFFICULTY.NORMAL;

		//NEW GAME SUBSCENE
		newGameSubScene = new SudokuSubScene();
		newGameSubScene.setLabel("DO YOU WANT A NEW SUDOKU?");
		newGameSubScene.setLayoutY(120);
		newGameSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		newGameSubScene.backgroundSettings(400,200);
		newGameSubScene.setTransitionCoordinate(-1329,0);
		
		ArrayList<SudokuButton> newGameButtons = new ArrayList<SudokuButton>();
		SudokuButton yesNewGame = new SudokuButton("YES");
		yesNewGame.setDifficulty(this.difficulty);
		yesNewGame.setDifficultyStyle();
		yesNewGame.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				removeHighlight();
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();

				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();

				gameManager.generateSudoku();
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				createGrid(gameManager.getGrid());
				newGameSubScene.moveSubScene();
				for(SudokuButton b : gameButtons) {
					if(b.isDisable())
						b.setDisable(false);
				}
				createSubScene();
				gameManager.restartTimer();
				animationTimer.start();
			}
		});
		
		SudokuButton noNewGame = new SudokuButton("NO");
		noNewGame.setDifficulty(this.difficulty);
		noNewGame.setDifficultyStyle();
		noNewGame.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				newGameSubScene.moveSubScene();
				for(SudokuCell c : grid.getCells())
					c.setDisable(false);
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(false);
				}
				gameManager.startTimer();
				animationTimer.start();
			}
		});
		
		newGameButtons.add(yesNewGame);
		newGameButtons.add(noNewGame);
		newGameSubScene.addButtons(newGameButtons);

		VBox newGameBox = new VBox();
		newGameBox.setSpacing(10);
		newGameBox.setAlignment(Pos.CENTER);
		newGameBox.getChildren().addAll(newGameSubScene.getButtons());
		newGameBox.setLayoutX(100);
		newGameBox.setLayoutY(70);
		
		newGameSubScene.setLabelLayout(23,20);
		newGameSubScene.getPane().getChildren().add(newGameSubScene.getLabel());
		newGameSubScene.getPane().getChildren().add(newGameBox);
	
		pane.getChildren().add(newGameSubScene);
		
		//RESTART SUBSCENE
		restartSubScene = new SudokuSubScene();
		restartSubScene.setLabel("DO YOU WANT TO RESTART?");
		restartSubScene.setLayoutY(170);
		restartSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		restartSubScene.backgroundSettings(400,200);
		restartSubScene.setTransitionCoordinate(-1329,0);
		
		ArrayList<SudokuButton> restartButtons = new ArrayList<SudokuButton>();
		SudokuButton yesRestart = new SudokuButton("YES");
		SudokuButton noRestart = new SudokuButton("NO");
		if(difficulty != null) {
			yesRestart.setDifficulty(this.difficulty);
			yesRestart.setDifficultyStyle();
			noRestart.setDifficulty(this.difficulty);
			noRestart.setDifficultyStyle();
		}
		yesRestart.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();
				removeHighlight();

				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();
				grid.setCells(gameManager.getStartGrid());

				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons) 
					number.setCont(9);
				loadGrid();
				restartSubScene.moveSubScene();
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				createSubScene();
				gameManager.restartTimer();
				animationTimer.start();
			}
		});
		
		noRestart.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				restartSubScene.moveSubScene();
				for(SudokuCell c : grid.getCells())
					c.setDisable(!c.isDisable());
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(!n.isDisable());
				}
				gameManager.startTimer();
				animationTimer.start();
			}
		});

		restartButtons.add(yesRestart);
		restartButtons.add(noRestart);
		restartSubScene.addButtons(restartButtons);

		VBox restartBox = new VBox();
		restartBox.setSpacing(10);
		restartBox.setAlignment(Pos.CENTER);
		restartBox.getChildren().addAll(restartSubScene.getButtons());
		restartBox.setLayoutX(100);
		restartBox.setLayoutY(70);
		
		restartSubScene.setLabelLayout(25,30);
		restartSubScene.getPane().getChildren().add(restartSubScene.getLabel());
		restartSubScene.getPane().getChildren().add(restartBox);
	
		pane.getChildren().add(restartSubScene);
		
		//INFO
		infoSubScene = new SudokuSubScene();
		infoSubScene.setLabel("SuDoKu RuLeS");
		infoSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		infoSubScene.backgroundSettings(400,400);
		infoSubScene.setLayoutY(120);
		infoSubScene.setTransitionCoordinate(-1331, 0);
		ScrollPane scroll = new ScrollPane();
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scroll.setLayoutX(30);
		scroll.setLayoutY(60);
		scroll.setPrefSize(338, 270);
		AnchorPane scrollPane = new AnchorPane();
		String rules = "";
		FileInputStream rulesFile;
		try {
			rulesFile = new FileInputStream("rules/sudokuRules");
			DataInputStream data_input = new DataInputStream(rulesFile); 
			@SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input)); 
			String str_line;
			while ((str_line = buffer.readLine()) != null) 
			{ 
			    str_line = str_line.trim(); 
		        rules += str_line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Label l = new Label(rules);
		l.setStyle("-fx-font: 13px Arial;\n");
		scrollPane.getChildren().add(l);
		scroll.setContent(scrollPane);

		ArrayList<SudokuButton> infoButtons = new ArrayList<SudokuButton>();
		SudokuButton okInfo = new SudokuButton("OK");
		okInfo.setScaleX(0.7);
		okInfo.setScaleY(0.7);
		okInfo.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				infoSubScene.moveSubScene();
				for(SudokuButton b : gameButtons) {
					if(b.isDisable())
						b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(false);
				}
				
				gameManager.startTimer();
				animationTimer.start();
			}
		});
		infoButtons.add(okInfo);
		infoSubScene.addButtons(infoButtons);

		VBox infoBox = new VBox();
		infoBox.setSpacing(10);
		infoBox.setAlignment(Pos.BOTTOM_RIGHT);
		infoBox.getChildren().addAll(infoSubScene.getButtons());
		infoBox.setLayoutX(210);
		infoBox.setLayoutY(330);
		
		infoSubScene.getPane().getChildren().add(infoSubScene.getLabel());
		infoSubScene.getPane().getChildren().add(scroll);
		infoSubScene.getPane().getChildren().add(infoBox);
	
		pane.getChildren().add(infoSubScene);
		
		//FINISHGAME SUBSCENE
		highscoreSubScene = new SudokuSubScene();
		highscoreSubScene.setLabel("Congratulation!\nNEW HIGH SCORE!\nDo you want to save the score?" );//VA MESSO LO SCORE
		
		highscoreSubScene.setLayoutY(120);
		highscoreSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		highscoreSubScene.backgroundSettings(400,200);
		highscoreSubScene.setTransitionCoordinate(-1329,0);
		
		
		ArrayList<SudokuButton> highButtons = new ArrayList<SudokuButton>();
		SudokuButton yesHighScore = new SudokuButton("YES");
		SudokuButton noHighScore = new SudokuButton("NO");
		if(difficulty != null) {
			yesHighScore.setDifficulty(this.difficulty);
			yesHighScore.setDifficultyStyle();
			noHighScore.setDifficulty(this.difficulty);
			noHighScore.setDifficultyStyle();
		}
		
		
		yesHighScore.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();
				removeHighlight();

				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();
				grid.setCells(gameManager.getStartGrid());

				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons) 
					number.setCont(9);
				loadGrid();
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				
				highscoreSubScene.moveSubScene();
				createSubScene();

				insertIdSubScene.moveSubScene();

				
				
			}
		});
		
		
		noHighScore.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				for(SudokuCell c : grid.getCells())
					c.setDisable(!c.isDisable());
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(!n.isDisable());
				}
				
				highscoreSubScene.moveSubScene();
				winnerNotification();
			}
		});
		
		yesHighScore.setScaleX(0.8);
		yesHighScore.setScaleY(0.8);
		noHighScore.setScaleX(0.8);
		noHighScore.setScaleY(0.8);
		

		highButtons.add(yesHighScore);
		highButtons.add(noHighScore);
		
		highscoreSubScene.addButtons(highButtons);

		HBox highScoreBox = new HBox();
		highScoreBox.setAlignment(Pos.CENTER);
		highScoreBox.getChildren().addAll(highscoreSubScene.getButtons());
		highScoreBox.setLayoutX(10);
		highScoreBox.setLayoutY(130);
		
		
		
//		highscoreSubScene.getPane().getChildren().add(yesHighScore);
//		highscoreSubScene.getPane().getChildren().add(noHighScore);
		highscoreSubScene.setLabelLayout(25,30);
		highscoreSubScene.getPane().getChildren().add(highscoreSubScene.getLabel());
		highscoreSubScene.getPane().getChildren().add(highScoreBox);
	
		pane.getChildren().add(highscoreSubScene);
		


		
		
		
		
		//RANKING SUBSCENE
		rankingSubScene = new SudokuSubScene();
		rankingSubScene.setLabel("RANKING");
		rankingSubScene.setLayoutY(170);
		rankingSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		rankingSubScene.backgroundSettings(400,350);
		rankingSubScene.setTransitionCoordinate(-1329,0);
		
		Label ranking = new Label();
		
		ranking.setLayoutX(50);
		ranking.setLayoutY(80);
		
		ranking.setText(gameManager.getScores().toString());
		System.out.println(ranking.getText());
		
		ArrayList<SudokuButton> rankingButtons = new ArrayList<SudokuButton>();
		SudokuButton rankingOkButton = new SudokuButton("OK");
		
		
		if(difficulty != null) {
			rankingOkButton.setDifficulty(this.difficulty);
			rankingOkButton.setDifficultyStyle();
		}
		
		rankingOkButton.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();
				removeHighlight();

				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();
				grid.setCells(gameManager.getStartGrid());

				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons) 
					number.setCont(9);
				loadGrid();
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				
				rankingSubScene.moveSubScene();

				winnerNotification();
			}
		});
		
			
			
			rankingButtons.add(rankingOkButton);
			rankingSubScene.addButtons(rankingButtons);
			

			VBox rankingVBox = new VBox();
			
			rankingVBox.setSpacing(10);
			rankingVBox.setAlignment(Pos.CENTER);
			rankingVBox.getChildren().addAll(rankingSubScene.getButtons());
			rankingVBox.setLayoutX(100);
			rankingVBox.setLayoutY(250);
			
			rankingSubScene.setLabelLayout(25,30);
			rankingSubScene.getPane().getChildren().add(rankingSubScene.getLabel());
			rankingSubScene.getPane().getChildren().add(rankingVBox);
			rankingSubScene.getPane().getChildren().add(ranking);
		
			pane.getChildren().add(rankingSubScene);
	
			//insertID SUBSCENE
			insertIdSubScene = new SudokuSubScene();
			insertIdSubScene.setLabel("INSERISCI IL TUO NOME: ");
			insertIdSubScene.setLayoutY(170);
			insertIdSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
			insertIdSubScene.backgroundSettings(400,200);
			insertIdSubScene.setTransitionCoordinate(-1329,0);
			
			ArrayList<SudokuButton> insertIdButtons = new ArrayList<SudokuButton>();
			SudokuButton insertButton = new SudokuButton("INSERT");
			
	    	TextArea nameTextAria=TextAreaBuilder.create()
	                .prefWidth(200).prefHeight(10)
	                .wrapText(true)
	                .build();
	    	
	    	nameTextAria.setLayoutX(110);
	    	nameTextAria.setLayoutY(80);
	    
			

			if(difficulty != null) {
				insertButton.setDifficulty(this.difficulty);
				insertButton.setDifficultyStyle();
			}
			
			insertButton.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{

					
					userName=nameTextAria.getText();
					
					/////////////////////////////////////////////////////
					//SALVIAMO IL NOME SCRITTO ALL'INTERNO DEL TEXTBOXE//
					/////////////////////////////////////////////////////						
							
					gameManager.getActualScore().setUser(userName);
					
					
					/////////////////////////////////////////
					//INSERISCI IL PUNTEGGIO NELLA CLASSIFICA//
					/////////////////////////////////////////
					
					gameManager.getScores().addScore(gameManager.getActualScore());
					
					
					insertIdSubScene.moveSubScene();
					ranking.setText(gameManager.getScores().toString());
					
					rankingSubScene.moveSubScene();
				}
			});
			
				
				insertButton.setScaleX(0.8);
				insertButton.setScaleY(0.8);
				
				insertIdButtons.add(insertButton);
				insertIdSubScene.addButtons(insertIdButtons);

				VBox insertIdBox = new VBox();

				insertIdBox.setAlignment(Pos.BOTTOM_RIGHT);
				insertIdBox.getChildren().addAll(insertIdSubScene.getButtons());
				insertIdBox.setLayoutX(110);
				insertIdBox.setLayoutY(140);
				
				insertIdSubScene.setLabelLayout(25,30);
				insertIdSubScene.getPane().getChildren().add(insertIdSubScene.getLabel());
				insertIdSubScene.getPane().getChildren().add(insertIdBox);
				insertIdSubScene.getPane().getChildren().add(nameTextAria);
			
				pane.getChildren().add(insertIdSubScene);
	}

	private void createGrid(ArrayList<Cell> cells) 
	{
		if(!grid.getCells().isEmpty()){
			int x;
			int y = 130;
			//creazione e disposizione grafica delle celle
			for(int r = 0; r < 9; r++) {
				x = 190;
				for (int c = 0; c < 9; c++) {
					for(Cell cell : cells) {
						if(cell.getRow() == r && cell.getColumn() == c){
							/* dovendo rendere la cella final per poterle assegnare il listener 
							** le celle di GameView e quelle di GameManager sono le stesse come se fossero statiche*/
							boolean startGrid = false;
							for(SudokuCell sudokuCell : grid.getCells()){
								if(sudokuCell.checkPosition(r,c)){
									sudokuCell.setLayout(x,y);
									sudokuCell.setStartFont();
									sudokuCell.showContent();
									startGrid = true;
									break;
								}
							}
							if(!startGrid) {
								final SudokuCell sudokuCell = new SudokuCell(r,c, cell.getValue());
								sudokuCell.setLayout(x,y);
								grid.add(sudokuCell);
							}
						}
					}
					if(c == 2 || c == 5)
						x += 50;
					else
						x += 40;
				}
				if(r == 2 || r == 5)
					y += 45;
				else
					y += 38;
			}
			//aggiungo le funzioni da richiamare alla selezione della cella
			for(SudokuCell sudokuCell : grid.getCells()){
				addCellListener(sudokuCell);
				SudokuCell cell = new SudokuCell(sudokuCell.getRow(), sudokuCell.getColumn(), sudokuCell.getValue());
				if(sudokuCell.isHide())
					cell.hideContent();
				else
					cell.showContent();
				for(NumberButton number : numberButtons){
					/*in base alle celle visibili assegno un contatore al NumberButton corrispondente
					**per tenere traccia delle volte che posso utilizzarlo prima di esaurirlo*/
					if(number.getValue() == sudokuCell.getValue() && !sudokuCell.isHide())
						number.setCont(number.getCont()-1);
				}
				gameManager.addToStartGrid(cell);
			}
			pane.getChildren().addAll(grid.getCells());
			gameManager.setSudokuCells(grid.getCells());
			grid.setState(grid);
			gameManager.getCareTaker().add(grid.getState());
		}
		else {
			//in base alla difficoltà decido il numero di celle in cui è visibile il valore
			int cellToShow = gameManager.computeVisibleCells();
			int x;
			int y = 130;
			//creazione e disposizione grafica delle celle
			for(int r = 0; r < 9; r++){
				x = 190;
				for (int c = 0; c < 9; c++) {
					for(Cell cell : cells) {
						if(cell.getRow() == r && cell.getColumn() == c){
							/* dovendo rendere la cella final per poterle assegnare il listener 
							** le celle di GameView e quelle di GameManager sono le stesse come se fossero statiche*/
							final SudokuCell sudokuCell = new SudokuCell(r,c, cell.getValue());
							sudokuCell.setLayout(x,y);
							if(c == 2 || c == 5)
								x += 50;
							else
								x += 40;
							//aggiungo le funzioni da richiamare alla selezione della cella
							addCellListener(sudokuCell);
							grid.add(sudokuCell);
						}
					}
				}
				if(r == 2 || r == 5)
					y += 45;
				else
					y += 38;
			}
			//funzione per determinare quale cella è visibile e quale no
			setVisibleCell(cellToShow);
			for(SudokuCell cell : grid.getCells()){
				/* creo una sudokuCell per poterla assegnare alla griglia di partenza del GameManager
				** non posso passare direttamente sudokuCells perchè altrimenti modificherei anche la griglia
				** di parteza visto che ogni cella con listener è final */
				SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
				if(!cell.isHide()){
					sudokuCell.showContent();
					cell.setStartFont();
					for(NumberButton number : numberButtons){
						/*in base alle celle visibili assegno un contatore al NumberButton corrispondente
						**per tenere traccia delle volte che posso utilizzarlo prima di esaurirlo*/
						if(number.getValue() == cell.getValue())
							number.setCont(number.getCont()-1);
					}
				}
				gameManager.addToStartGrid(sudokuCell);
			}
			pane.getChildren().addAll(grid.getCells());
			gameManager.setSudokuCells(grid.getCells());
			grid.setState(grid);
			gameManager.getCareTaker().add(grid.getState());
		}
	}	
	
	private void loadGrid() 
	{
		gameManager.setSudokuCells(grid.getCells());
		int x;
		int y = 130;

		//creazione e disposizione delle celle grafiche
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(SudokuCell cell : grid.getCells())
				{
					if(cell.getRow() == r && cell.getColumn() == c)
					{
						cell.setScale(1.2,1.2);
						cell.setLayoutX(x);
							
						if(c == 2 || c == 5)
							x += 50;
						else
							x += 40;
	
						cell.setLayoutY(y);
						if(!cell.isHide()) 
						{
							if(cell.getValue() == cell.getAssignedValue()) {
								cell.setStartFont();
								cell.showContent();
							}else {
								cell.setAssignedValue(cell.getAssignedValue());
							}
							for(NumberButton number : numberButtons)
							{
								if(number.getValue() == cell.getValue())
									number.setCont(number.getCont()-1);
							}
						}
						else
							cell.removeContent();
						
						SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
						sudokuCell.setAssignedValue(cell.getAssignedValue());
						if(!cell.isHide()) 
							sudokuCell.showContent();
						
						gameManager.addToStartGrid(sudokuCell);
						addCellListener(cell);
					}
				}
			}
			if(r == 2 || r == 5)
				y += 45;
			else
				y += 38;
		}
		for(SudokuCell c : gameManager.getSudokuCells()) 
		{
			if(!c.isHide()) 
			{
				for(SudokuCell sc : grid.getCells()) 
				{
					if(sc.checkPosition(c.getRow(), c.getColumn())) {
						if(sc.isHide()) {
							sc.setAssignedValue(c.getAssignedValue());
							SudokuCell s = new SudokuCell(sc.getRow(),sc.getColumn(),sc.getValue());
							s.setAssignedValue(c.getAssignedValue());
							addCellListener(s);
							gameManager.addToStartGrid(s);
						}
					}
				}
			}
		}
		pane.getChildren().addAll(grid.getCells());
		grid.setState(grid);
		gameManager.getCareTaker().add(grid.getState());
	}

	private void setVisibleCell(int cellToShow) 
	{
		while(cellToShow > 0) 
		{
			for(SudokuCell cell : grid.getCells())
			{
				boolean bool = new Random().nextBoolean();
				if(cellToShow > 0 && cell.isHide()) 
				{
					if(bool) 
					{
						cell.showContent();
						cellToShow -= 1;
					}
				}
			}
		}
	}

	private void addCellListener(final SudokuCell sudokuCell) 
	{
		sudokuCell.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				removeHighlight();
				if(sudokuCell.isHide()) 
				{
					gameManager.setSelectedCell(sudokuCell);
					//richiamo una funzione di GameManager per controllare che il valore selezionato possa essere inserito
					if(!gameManager.setSudokuCellValue(sudokuCell, gameManager.getValue())) 
					{
						highlightCell(gameManager.getCellWithSameValue());
					}
					else {
						removeHighlight();
//**********************/GESTIONE MEMENTO/******************************************/
						sudokuCell.setAssignedValue(gameManager.getValue());
						grid.setState(grid);
						gameManager.increaseIteration();
						gameManager.getCareTaker().add(grid.getState());
						
						for(NumberButton number : numberButtons) 
						{
							if(!number.isEmpty()) 
							{
								if(number.getValue() == sudokuCell.getAssignedValue()) 
								{
									number.setCont(number.getCont()-1);
									if(number.isEmpty()) 
									{
										gameManager.setValue(0);
				     					 if(gameManager.getHideCells() == 0) 
											{
												
												///////////////////////////////////////////////////////////////
												//CONTROLLO SE IL PUNTEGGIO FATTO È IL MIGLIORE IN CLASSIFICA//
												///////////////////////////////////////////////////////////////
												
				     						gameManager.setActialScore(new Score("", new TimeScored(gameManager.getTimer()), difficulty)); ;
											isBestScore = gameManager.getScores().isHighScore(gameManager.getActualScore());
												
												if(isBestScore) {
													isBestScore=false;
													highscoreSubScene.moveSubScene();
												}
												else {
													winnerNotification();
												}
												gameManager.stopTimer();
												animationTimer.stop();
											}
									}
								}
							}
						}
					}
					animationTimer.start();
					gameManager.StartTimerFromZero();
				}
				else {
					gameManager.setSelectedCell(sudokuCell);
				}
			}
		});	
	}
		
	private void highlightCell(ArrayList<Point> sameValue) 
	{
		if(!sameValue.isEmpty()) 
		{
			for(Point coordinate : sameValue) 
			{
				for(SudokuCell cell : grid.getCells())
				{
					if(cell.checkPosition(coordinate.getX(),coordinate.getY())) 
						cell.highlightCell();
				}
			}
		}
	}
	
	private void removeHighlight() 
	{
		for(SudokuCell cell : grid.getCells())
		{
			if(cell.isHighlighted())
				cell.removeHiglight();
		}
	}
	
	private void showValue(SudokuCell selectedCell) 
	{
		
		gameManager.getCellWithSameValue().clear();
		for(SudokuCell cell : grid.getCells())
		{
			if(cell.checkPosition(selectedCell.getRow(), selectedCell.getColumn()) && cell.isHide())
			{
				if(!gameManager.checkValue(selectedCell,cell.getValue())) 
				{
					gameManager.addPenality();
					removeHighlight();
					highlightCell(gameManager.getCellWithSameValue());
					return;
				}
				cell.showContent();
//**********************/GESTIONE MEMENTO/******************************************/
				gameManager.increaseIteration();
				grid.setState(grid);
				gameManager.getCareTaker().add(grid.getState());
//**********************/GESTIONE MEMENTO/******************************************/
				gameManager.addPenality();
				cell.showContent();
				for(NumberButton number : numberButtons) 
				{
					if(number.getValue() == cell.getAssignedValue())
					{
						number.setCont(number.getCont()-1);
						if(number.getCont() == 0)
						{
							if(gameManager.getHideCells() == 0) 
							{
								
								///////////////////////////////////////////////////////////////
								//CONTROLLO SE IL PUNTEGGIO FATTO È IL MIGLIORE IN CLASSIFICA//
								///////////////////////////////////////////////////////////////
								
								
								gameManager.setActialScore(new Score("", new TimeScored(gameManager.getTimer()), difficulty)); ;
								
								
				
								isBestScore = gameManager.getScores().isHighScore(gameManager.getActualScore());
								if(isBestScore) {
									isBestScore=false;
									highscoreSubScene.moveSubScene();
								}
								else {
									winnerNotification();
								}
								gameManager.upgradeTimer();
								text.setText(gameManager.getTimerString());
								gameManager.upgradeTimer();
								text.setText(gameManager.getTimerString());
								gameManager.stopTimer();
								animationTimer.stop();
							}
							
						}else {
							return;
						}
					}
				}
			}
		}
	}

	private void winnerNotification() 
	{
		SudokuSubScene winSubScene = new SudokuSubScene();
		winSubScene.setLabel("SUDOKU COMPLETED! \n" + "your time is " + gameManager.getTimerString() );
		winSubScene.getLabel().setStyle("-fx-text-fill: Gold;");
		winSubScene.setLabelLayout(115,30);
		winSubScene.setLayoutY(127.5);
		winSubScene.setTransitionCoordinate(-1355, 0);

		ArrayList<SudokuButton> buttons = new ArrayList<SudokuButton>();
		SudokuButton restart = new SudokuButton("RESTART");
		restart.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				for(SudokuButton button : gameButtons) {
					button.setDisable(false);
				}
				restart.setLayoutY(173.0);
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();
				removeHighlight();
				
				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();
				grid.setCells(gameManager.getStartGrid());
				
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				winSubScene.moveSubScene();
				loadGrid();
				createSubScene();
				gameManager.restartTimer();
				animationTimer.start();
			}
		});
		buttons.add(restart);
		SudokuButton newGame = new SudokuButton("NEW GAME");
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				for(SudokuButton button : gameButtons) {
					button.setDisable(false);
				}
				gameManager.selectedValue(0);
				gameManager.getCellWithSameValue().clear();
				
				ArrayList<SudokuCell> cellToErase = new ArrayList<>();
				for(Object o : pane.getChildren()) {
					if(o instanceof SudokuCell)
						cellToErase.add((SudokuCell) o);
				}
				pane.getChildren().removeAll(cellToErase);
				grid = new SudokuGrid();
				gameManager.clearCaretaker();

				gameManager.generateSudoku();
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				createGrid(gameManager.getGrid());
				winSubScene.moveSubScene();
				createSubScene();
				gameManager.restartTimer();
				animationTimer.start();
			}
		});
		buttons.add(newGame);
		SudokuButton menu = new SudokuButton("BACK TO MENU");
		menu.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				hiddenStage.show();
				stage.close();
				animationTimer.stop();
				gameManager.restartTimer();
			}
		});
		buttons.add(menu);
		winSubScene.addButtons(buttons);
		
		VBox buttonsBox = new VBox();
		buttonsBox.setSpacing(20);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.getChildren().addAll(winSubScene.getButtons());
		buttonsBox.setLayoutX(130);
		buttonsBox.setLayoutY(100);
		
		winSubScene.moveSubScene();
		winSubScene.getPane().getChildren().add(winSubScene.getLabel());
		winSubScene.getPane().getChildren().add(buttonsBox);
		pane.getChildren().add(winSubScene);
		
		for(SudokuButton button : gameButtons) {
			button.setDisable(true);
		}
	}

	private void saveNotification() 
	{
		SudokuSubScene saveSubScene = new SudokuSubScene();
		
		saveSubScene = new SudokuSubScene();
		saveSubScene.setLabel("SAVED GAME !");
		saveSubScene.setLabelLayout(21, 35);
		saveSubScene.setLayout(1522, 252);
		saveSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		saveSubScene.backgroundSettings(200, 100);
		saveSubScene.setTransitionCoordinate(-1250, 0);
		saveSubScene.getPane().getChildren().add(saveSubScene.getLabel());

		pane.getChildren().add(saveSubScene);
		
		for(SudokuButton button : gameButtons)
			button.setDisable(true);
		for(SudokuCell c : grid.getCells())
			c.setDisable(true);
		for(NumberButton n : numberButtons)
			n.setDisable(true);

		saveSubScene.moveSubScene();

		hiddenStage.show();
		stage.close();
	}
	
	public void setGameManager(GameManager gameManager) { this.gameManager = gameManager; }
	
	public void setDifficulty(DIFFICULTY diff) { 
		this.difficulty = diff;
		gameManager.setDifficulty(difficulty);
	}
	
	public Stage getStage() { return stage; }

	public void loadTimer(String timer) {
		gameManager.setTimer(timer);
		createTimerLabel();
	}
}
