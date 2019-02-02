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
import gui.model.SudokuSubScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logic.ai.Cell;
import logic.ai.GameManager;

@SuppressWarnings({ "restriction" })
public class GameView extends ViewManager implements IView {
	//ogni classe ha un proprio stage. Altri campi comuni a più classi sono nel ViewManager
	private Stage stage;

	private ArrayList<SudokuButton> gameButtons;
	private ArrayList<NumberButton> numberButtons;
	private SudokuSubScene newGameSubScene;
	private SudokuSubScene restartSubScene;
	private SudokuSubScene infoSubScene;
	
	private DIFFICULTY difficulty;
	private ArrayList<SudokuCell> sudokuCells;

	public GameView(DIFFICULTY difficulty) 
	{
		this.stage = new Stage();

		this.difficulty = difficulty;

		this.sudokuCells = new ArrayList<SudokuCell>();
		this.gameButtons = new ArrayList<SudokuButton>();
		this.numberButtons = new ArrayList<NumberButton>();
//GameManager contenuto in ViewManager per comunicazione tra grafica e logica
		this.gameManager = new GameManager();
//funzione di GameManager per la generazione del sudoku con DLV
		this.gameManager.generateSudoku();
		this.gameManager.setDifficulty(this.difficulty);
		createBackground();
		createButtons();
//creazione del sudoku con lista di Cell passate dal GameManager
		createGrid(this.gameManager.getGrid());
		createSubScene();

		this.stage.setScene(scene);
		this.stage.show();
	}

	//costruttore da utilizzare per caricare la partita
	public GameView(ArrayList<SudokuCell> sudokuCells) 
	{
		stage = new Stage();

		gameManager = new GameManager();
		
		this.sudokuCells = new ArrayList<SudokuCell>();
		this.sudokuCells = sudokuCells;

		this.gameButtons = new ArrayList<SudokuButton>();
		this.numberButtons = new ArrayList<NumberButton>();

		createBackground();
		createButtons();
		if(!needSolution()) {
			loadGrid();
		}
		else {
			if(gameManager.getSolution(gameManager.parseToCell(sudokuCells)))
				createGrid(gameManager.getGrid());
			else
				System.out.println("NO SOLUTION");
				//comunicare che non c'è soluzione
		}

		stage.setScene(scene);
		stage.show();
	}

	private boolean needSolution() {
		if(sudokuCells.size() != 81)
			return true;
		return false;
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
				}
			}
			
		});
				
		SudokuButton undoBtn = new SudokuButton("undo");
		undoBtn.setLayoutX(200);
		undoBtn.setLayoutY(60);
		undoBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {

			}
		});
		gameButtons.add(undoBtn);
		
		SudokuButton redoBtn = new SudokuButton("redo");
		redoBtn.setLayoutX(350);
		redoBtn.setLayoutY(60);	
		redoBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
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
					for(SudokuCell c : sudokuCells)
						c.setDisable(!c.isDisable());
					for(SudokuButton b : gameButtons) {
						b.setDisable(!b.isDisable());
					}
					for(NumberButton n : numberButtons) {
						if(!n.isEmpty())
							n.setDisable(!n.isDisable());
					}
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
					for(SudokuCell c : sudokuCells)
						c.setDisable(!c.isDisable());
					for(SudokuButton b : gameButtons) {
						b.setDisable(!b.isDisable());
					}
					for(NumberButton n : numberButtons) {
						if(!n.isEmpty())
							n.setDisable(!n.isDisable());
					}
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
					for(NumberButton number : numberButtons) {
						if(number.getValue() == var) {
							number.setCont(number.getCont()+1);
							return;
						}
					}
				}
			}
		});
				
		SudokuButton stopBtn = new SudokuButton("STOP");
		stopBtn.setLayoutX(200);
		stopBtn.setLayoutY(530);		
		gameButtons.add(stopBtn);	

		SudokuButton playBtn = new SudokuButton("PLAY");
		playBtn.setLayoutX(350);
		playBtn.setLayoutY(530);		
		gameButtons.add(playBtn);

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
					gameManager.saveGame(difficulty, sudokuCells);
					saveNotification();
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
				pane.getChildren().removeAll(sudokuCells);
				sudokuCells = new ArrayList<SudokuCell>();
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
				for(SudokuCell c : sudokuCells)
					c.setDisable(false);
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(false);
				}
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
				pane.getChildren().removeAll(sudokuCells);
				sudokuCells = new ArrayList<SudokuCell>();
				sudokuCells = gameManager.getStartGrid();
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				loadGrid();
				restartSubScene.moveSubScene();
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				createSubScene();
			}
		});
		
		noRestart.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				restartSubScene.moveSubScene();
				for(SudokuCell c : sudokuCells)
					c.setDisable(!c.isDisable());
				for(SudokuButton b : gameButtons) {
					b.setDisable(false);
				}
				for(NumberButton n : numberButtons) {
					if(!n.isEmpty())
						n.setDisable(!n.isDisable());
				}
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
	}

	private void createGrid(ArrayList<Cell> cells) 
	{
		if(!sudokuCells.isEmpty()) 
		{
			int x;
			int y = 130;

			//creazione e disposizione grafica delle celle
			for(int r = 0; r < 9; r++) 
			{
				x = 190;
				for (int c = 0; c < 9; c++) 
				{
					for(Cell cell : cells) 
					{
						if(cell.getRow() == r && cell.getColumn() == c)
						{
							/* dovendo rendere la cella final per poterle assegnare il listener 
							** le celle di GameView e quelle di GameManager sono le stesse come se fossero statiche*/
							boolean startGrid = false;
							for(SudokuCell sudokuCell : sudokuCells) 
							{
								if(sudokuCell.checkPosition(r,c)) 
								{
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
								sudokuCells.add(sudokuCell);
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
			for(SudokuCell sudokuCell : sudokuCells) 
			{
				addCellListener(sudokuCell);
				SudokuCell cell = new SudokuCell(sudokuCell.getRow(), sudokuCell.getColumn(), sudokuCell.getValue());
				if(sudokuCell.isHide())
					cell.hideContent();
				else
					cell.showContent();
				for(NumberButton number : numberButtons)
				{
					/*in base alle celle visibili assegno un contatore al NumberButton corrispondente
					**per tenere traccia delle volte che posso utilizzarlo prima di esaurirlo*/
					if(number.getValue() == sudokuCell.getValue() && !sudokuCell.isHide())
						number.setCont(number.getCont()-1);
				}
				gameManager.addToStartGrid(cell);
			}
			
			pane.getChildren().addAll(sudokuCells);
			gameManager.setSudokuCells(sudokuCells);
		}
		else {
			//in base alla difficoltà decido il numero di celle in cui è visibile il valore
			int cellToShow = gameManager.computeVisibleCells();
			int x;
			int y = 130;
	
			//creazione e disposizione grafica delle celle
			for(int r = 0; r < 9; r++) 
			{
				x = 190;
				for (int c = 0; c < 9; c++) 
				{
					for(Cell cell : cells) 
					{
						if(cell.getRow() == r && cell.getColumn() == c)
						{
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
							sudokuCells.add(sudokuCell);
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
	
			for(SudokuCell cell : sudokuCells) 
			{
				/* creo una sudokuCell per poterla assegnare alla griglia di partenza del GameManager
				** non posso passare direttamente sudokuCells perchè altrimenti modificherei anche la griglia
				** di parteza visto che ogni cella con listener è final */
				SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
				if(!cell.isHide()) 
				{
					sudokuCell.showContent();
					cell.setStartFont();
					for(NumberButton number : numberButtons)
					{
						/*in base alle celle visibili assegno un contatore al NumberButton corrispondente
						**per tenere traccia delle volte che posso utilizzarlo prima di esaurirlo*/
						if(number.getValue() == cell.getValue())
							number.setCont(number.getCont()-1);
					}
				}
				gameManager.addToStartGrid(sudokuCell);
			}
			pane.getChildren().addAll(sudokuCells);
			gameManager.setSudokuCells(sudokuCells);
		}
	}	
	
	private void loadGrid() 
	{
		gameManager.setSudokuCells(sudokuCells);

		int x;
		int y = 130;

		//creazione e disposizione delle celle grafiche
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(SudokuCell cell : sudokuCells) 
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
						}
						else
							cell.removeContent();
						
						SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
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
		pane.getChildren().addAll(sudokuCells);
	}
	
	private void setVisibleCell(int cellToShow) 
	{
		while(cellToShow > 0) 
		{
			for(SudokuCell cell : sudokuCells) 
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
//**********************/System.out.println("Set memento game view");//******************************************/
						sudokuCell.setAssignedValue(gameManager.getValue());
						
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
											winnerNotification();
										}
									}
								}
							}
						}
					}
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
				for(SudokuCell cell : sudokuCells) 
				{
					if(cell.checkPosition(coordinate.getX(),coordinate.getY())) 
						cell.highlightCell();
				}
			}
		}
	}
	
	private void removeHighlight() 
	{
		for(SudokuCell cell : sudokuCells) 
		{
			if(cell.isHighlighted())
				cell.removeHiglight();
		}
	}
	
	private void showValue(SudokuCell selectedCell) 
	{
		gameManager.getCellWithSameValue().clear();
		for(SudokuCell cell : sudokuCells) 
		{
			if(cell.checkPosition(selectedCell.getRow(), selectedCell.getColumn()) && cell.isHide())
			{
				if(!gameManager.checkValue(selectedCell,cell.getValue())) 
				{
					removeHighlight();
					highlightCell(gameManager.getCellWithSameValue());
					return;
				}
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
								winnerNotification();
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
		winSubScene.setLabel("SUDOKU COMPLETATO! \n" + "il tuo tempo e' xx:xx" );
		winSubScene.getLabel().setStyle("-fx-text-fill: Gold;");
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
				pane.getChildren().removeAll(sudokuCells);
				sudokuCells = new ArrayList<SudokuCell>();
				sudokuCells = gameManager.getStartGrid();
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				loadGrid();
				winSubScene.moveSubScene();
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
				pane.getChildren().removeAll(sudokuCells);
				sudokuCells = new ArrayList<SudokuCell>();
				gameManager.generateSudoku();
				gameManager.clearStartGrid();
				for(NumberButton number : numberButtons)
					number.setCont(9);
				createGrid(gameManager.getGrid());
				winSubScene.moveSubScene();
			}
		});
		buttons.add(newGame);
		SudokuButton menu = new SudokuButton("BACK TO MENU");
		menu.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				hiddenStage.show();
				stage.close();
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
		
		for(SudokuButton button : gameButtons) {
			button.setDisable(true);
		}
		for(SudokuCell c : sudokuCells) {
			c.setDisable(true);
		}
		for(NumberButton n : numberButtons) {
			n.setDisable(true);
		}
		saveSubScene.moveSubScene();
		//wait
		hiddenStage.show();
		stage.close();
	}
	
	public void setGameManager(GameManager gameManager) { this.gameManager = gameManager; }
	
	public void setDifficulty(DIFFICULTY diff) { 
		this.difficulty = diff;
		gameManager.setDifficulty(difficulty);
	}
	
	public Stage getStage() { return stage; }
}
