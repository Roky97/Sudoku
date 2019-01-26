package gui.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import gui.model.DIFFICULTY;
import gui.model.NumberButton;
import gui.model.SudokuButton;
import gui.model.SudokuCell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
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

		createBackground();
		createButtons();
//creazione del sudoku con lista di Cell passate dal GameManager
		createGrid(this.gameManager.getGrid());

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
		loadGrid();
		
		stage.setScene(scene);
		stage.show();
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
		SudokuButton backBtn = new SudokuButton("< back");
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
		gameButtons.add(undoBtn);
		
		SudokuButton redoBtn = new SudokuButton("redo");
		redoBtn.setLayoutX(350);
		redoBtn.setLayoutY(60);		
		gameButtons.add(redoBtn);		

		SudokuButton infoBtn = new SudokuButton("info");
		infoBtn.setLayoutX(590);
		infoBtn.setLayoutY(20);		
		gameButtons.add(infoBtn);

		final SudokuButton newGameBtn = new SudokuButton("new game");
		newGameBtn.setLayoutX(590);
		newGameBtn.setLayoutY(120);		
		gameButtons.add(newGameBtn);
		newGameBtn.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) 
				{
					newGameBtn.setLayoutY(123.0 );		
					gameManager.selectedValue(0);
					gameManager.generateSudoku();
					createGrid(gameManager.getGrid());
				}
			}
		});

		final SudokuButton restartBtn = new SudokuButton("restart");
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
					gameManager.selectedValue(0);
					sudokuCells = gameManager.getStartGrid();

					loadGrid();
				}
			}
		});

		SudokuButton hintBtn = new SudokuButton("hint");
		hintBtn.setLayoutX(590);
		hintBtn.setLayoutY(220);		
		gameButtons.add(hintBtn);		
		
		SudokuButton deleteBtn = new SudokuButton("delete");
		deleteBtn.setLayoutX(590);
		deleteBtn.setLayoutY(480);		
		gameButtons.add(deleteBtn);
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				int var = gameManager.getSelectedCell().getAssignedValue();
				if(gameManager.removeContent(gameManager.getSelectedCell())) 
				{
					for(NumberButton number : numberButtons) {
						if(number.getValue() == var) {
							number.setCont(number.getCont()+1);
							return;
						}
					}
				}
				gameManager.setValue(0);
			}
		});
				
		SudokuButton stopBtn = new SudokuButton("stop");
		stopBtn.setLayoutX(200);
		stopBtn.setLayoutY(530);		
		gameButtons.add(stopBtn);	

		SudokuButton playBtn = new SudokuButton("play");
		playBtn.setLayoutX(350);
		playBtn.setLayoutY(530);		
		gameButtons.add(playBtn);

		final SudokuButton saveBtn = new SudokuButton("save");
		saveBtn.setLayoutX(590);
		saveBtn.setLayoutY(530);		
		gameButtons.add(saveBtn);
		saveBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					saveBtn.setLayoutY(533.0);
					gameManager.saveGame(difficulty, sudokuCells);
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
			final NumberButton b = new NumberButton(i);
			b.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					removeHighlight();
					if(!b.isEmpty()) {
						gameManager.selectedValue(b.getValue());
					}
					else {
						gameManager.setValue(0);
					}
					
				}
			});
			b.setLayoutX(pos);
			b.setLayoutY(492);
			pos += 47;
			numberButtons.add(b);
		}
		pane.getChildren().addAll(numberButtons);
	}

	private void createGrid(ArrayList<Cell> cells) 
	{
		//in base alla difficoltà decido il numero di celle in cui è visibile il valore
		int cellToShow = 0;
		Random rand = new Random();
		switch (difficulty)
		{
			case EASY:
				cellToShow = rand.nextInt(55-50)+50;
				break;
			case NORMAL:
				cellToShow = rand.nextInt(40-35)+35;
				break;
			case HARD:
				cellToShow = rand.nextInt(27-25)+25;
				break;
				
			default:
				break;
		}

		int x;
		int y = 130;

//		pane.getChildren().remove(sudokuCells);
//		sudokuCells.clear();
//		gameManager.getStartGrid().clear();

		//creazione e disposizione delle celle grafiche
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(Cell cell : cells) 
				{
					if(cell.getRow() == r && cell.getColumn() == c)
					{
						final SudokuCell sudokuCell = new SudokuCell(r,c, cell.getValue());
						sudokuCell.setLayoutX(x);
							
						if(c == 2 || c == 5)
							x += 50;
						else
							x += 40;

						sudokuCell.setLayoutY(y);
						//aggiungo le funzioni da richiamare alla selezione della cella
						sudokuCell.setOnAction(new EventHandler<ActionEvent>() 
						{
							public void handle(ActionEvent event) 
							{
								removeHighlight();
								if(sudokuCell.isHide()) 
								{
									//richiamo una funzione di GameManager per controllare che il valore selezionato possa essere inserito
									if(!gameManager.setSudokuCellValue(sudokuCell, gameManager.getValue())) 
									{
										highlightCell(gameManager.getCellWithSameValue());
									}
									else {
										removeHighlight();
										sudokuCell.setAssignedValue(gameManager.getValue());
											
										for(NumberButton number : numberButtons) 
										{
											if(!number.isEmpty()) 
											{
												if(number.getValue() == sudokuCell.getAssignedValue()) 
												{
													number.setCont(number.getCont()-1);
													if(number.isEmpty())
														gameManager.setValue(0);
												}
											}
										}
										
										if(gameManager.getHideCells() == 0) {
											if(gameManager.solution()) {
												System.out.println("WIN");
											}
										}
									}
								}
								else {
									gameManager.setSelectedCell(sudokuCell);
								}
							}
						});
						sudokuCells.add(sudokuCell);
					}
				}
			}
			if(r == 2 || r == 5)
				y += 45;
			else
				y += 38;
		}

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

		for(SudokuCell cell : sudokuCells) 
		{
			SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
			if(!cell.isHide()) 
			{
				sudokuCell.showContent();
				for(NumberButton number : numberButtons)
				{
					if(number.getValue() == cell.getValue()) {
						number.setCont(number.getCont()-1);
					}
				}
			}
			this.gameManager.addToStartGrid(sudokuCell);
		}

		pane.getChildren().addAll(sudokuCells);
		gameManager.setSudokuCells(sudokuCells);
	}	
	
	public void setDifficulty(DIFFICULTY diff) {
		this.difficulty = diff;
	}
	
	private void loadGrid() 
	{
//		gameManager.getStartGrid().clear();
		System.out.println(sudokuCells.size());
		int cont = 0;
		for(SudokuCell c : sudokuCells) {
			if(c.isHide())
				cont++;
		}
		System.out.println(cont);
		int x;
		int y = 130;
		
		pane.getChildren().removeAll(sudokuCells);
		
		ArrayList<SudokuCell> startGrid = new ArrayList<SudokuCell>();
		
		for(int r = 0; r < 9; r++) 
		{
			x = 190;
			for (int c = 0; c < 9; c++) 
			{
				for(SudokuCell cell : sudokuCells) 
				{
					cell.setScaleX(1.2);
					cell.setScaleY(1.2);
					if(cell.getRow() == r && cell.getColumn() == c) 
					{
//						if(cell.isHide())
//							cell.hideContent();
//						else
//							cell.showContent();
						
						cell.setLayoutX(x);
							
						if(c == 2 || c == 5)
							x += 50;
						else
							x += 40;

						cell.setLayoutY(y);

						SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
						if(!cell.isHide()) 
							sudokuCell.showContent();
						startGrid.add(sudokuCell);
					}
				}
			}
			if(r == 2 || r == 5)
				y += 45;
			else
				y += 38;
		}
		gameManager.setStartGrid(startGrid);
		pane.getChildren().addAll(sudokuCells);	
	}

	public Stage getStage() {
		return stage;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	private void highlightCell(ArrayList<Point> sameValue) 
	{
		if(!sameValue.isEmpty()) 
		{
			for(Point coordinate : sameValue) 
			{
				for(SudokuCell cell : sudokuCells) 
				{
					if(cell.getRow() == coordinate.getX() && cell.getColumn() == coordinate.getY()) 
					{
						cell.highlightCell();
					}
				}
			}
		}
	}
	
	private void removeHighlight() {
		for(SudokuCell cell : sudokuCells) {
			if(cell.isHighlighted())
				cell.removeHiglight();
		}
	}

}
