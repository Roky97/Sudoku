package logic.ai;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import gui.model.DIFFICULTY;
import gui.model.SudokuCell;

public class GameManager {

	//Solver per la creazione di livelli
	private SudokuGenerator generator;

	private ArrayList<Cell> grid;
	
	private DIFFICULTY difficulty;
	private ArrayList<SudokuCell> sudokuCells;
	private ArrayList<SudokuCell> startGrid;
	private int value;
	private ArrayList<Point> sameValue;
	private SudokuCell selectedCell;
	
	public GameManager() 
	{
		setValue(0);
		generator = new SudokuGenerator();
		grid = new ArrayList<Cell>();
		sudokuCells = new ArrayList<SudokuCell>();
		sameValue = new ArrayList<Point>();
		startGrid = new ArrayList<SudokuCell>();
		selectedCell = new SudokuCell();
	}
	
	//generazione automatic di livelli con DLV
	public void generateSudoku()
	{
		if(generator.generateSudoku()) 
		{
			sudokuCells.clear();
			grid.clear();
			grid = generator.getGrid();
		}
	}
	
	public void selectedValue(int value) { this.setValue(value); }
	public void setValue(int value) { this.value = value; }
	public int getValue() { return this.value; }
	
	public boolean setSudokuCellValue(SudokuCell sudokuCell, int value)
	{
		if(sudokuCell.isHide()) {
			sameValue.clear();
			int index = sudokuCells.indexOf(sudokuCell);
			//restituisco il valore booleano delle funzioni di controllo
			return (checkValue(sudokuCells.get(index), value));
		}
		return false;
	}
	
	//controllo che il valore inserito nella cella rispetti le regole del gioco

	private boolean checkValue(SudokuCell sudokuCell, int value) 
	{
		int x = sudokuCell.getRow();
		int y = sudokuCell.getColumn();

		int r = 0;
		int c = 0;
		
		if(y >= 3 && y <= 5) 
			c = 3;
		else if(y >= 6)
			c = 6;
		
		if(x >= 3 && x <= 5) 
			r = 3;
		else if(x >= 6)
			r = 6;
		
		boolean subMatrix = checkOnSubMatrix(r,c,x,y,value);
		boolean rowAndColumn = checkOnRowAndColumn(x,y,value);
		
		return subMatrix && rowAndColumn;
	}

	private boolean checkOnRowAndColumn(int x, int y, int value) 
	{
		boolean insert = true;

		for(SudokuCell cell : sudokuCells) 
		{
			if(cell.getColumn() == y) 
			{
				if(cell.getRow() != x && cell.getAssignedValue() == value && !cell.isHide())
				{
					//aggiungo alla struttura dati le coordinate in cui ho valori uguali a quello selezionato
					sameValue.add(new Point(cell.getRow(),y));
					insert = false;
				}
			}
			if(cell.getRow() == x) 
			{
				if(cell.getColumn() != y && cell.getAssignedValue() == value && !cell.isHide())
				{
					//aggiungo alla struttura dati le coordinate in cui ho valori uguali a quello selezionato
					sameValue.add(new Point(x,cell.getColumn()));
					insert = false;
				}
			}
		}
		return insert;
	}

	private boolean checkOnSubMatrix(int r, int c, int x, int y, int value) 
	{
		boolean insert = true;

		for(int i = r; i <= r+2; i++) 
		{
			for(int j = c; j <= c+2; j++) 
			{
				for(SudokuCell cell : sudokuCells)
				{
					if(cell.getRow() != x && cell.getColumn() != y) 
					{
						if(cell.getRow() == i && cell.getColumn() == j && !cell.isHide()) 
						{
							if(cell.getAssignedValue() == value) 
							{
								//aggiungo alla struttura dati le coordinate in cui ho valori uguali a quello selezionato
								this.sameValue.add(new Point(cell.getRow(), cell.getColumn()));
								insert = false;
							}
						}
					}
				}
			}
		}
		return insert;
	}
		
	public ArrayList<Point> getCellWithSameValue() {
		return sameValue;
	}

	public void saveGame(DIFFICULTY difficulty, ArrayList<SudokuCell> sudokuCells) 
	{
		System.out.println("SAVE");
	      try {
	          FileOutputStream fileOut = new FileOutputStream("saves/game.ser");
	          ObjectOutputStream out = new ObjectOutputStream(fileOut);
	          out.writeObject(difficulty);
	          out.writeObject(sudokuCells);
	          out.close();
	          fileOut.close();
	          System.out.println("Serialized data is saved in saves/game.ser");
	       } catch (IOException i) {
	          i.printStackTrace();
	       }
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public boolean loadGame() 
	{
		try {
			FileInputStream fileIn;
			fileIn = new FileInputStream("saves/game.ser");
			if(fileIn.available() > 0) 
			{
				ObjectInputStream in = new ObjectInputStream(fileIn);
				this.difficulty = (DIFFICULTY) in.readObject();
				this.sudokuCells = (ArrayList<SudokuCell>) in.readObject();
				in.close();
				fileIn.close();
				System.out.println("Load successfull");
	
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("no load avaiable");
		return false;
	}

	public DIFFICULTY getDifficulty() { return this.difficulty; }

	public void setSudokuCells(ArrayList<SudokuCell> sudokuCells) { this.sudokuCells = sudokuCells; }
	public ArrayList<SudokuCell> getSudokuCells() { return this.sudokuCells; }

	public void setGrid(ArrayList<SudokuCell> sudokuCells) { this.sudokuCells = sudokuCells; }
	public ArrayList<Cell> getGrid() { return grid; }

	public void addToStartGrid(SudokuCell sudokuCell) {
		startGrid.add(sudokuCell);
	}

	public ArrayList<SudokuCell> getStartGrid() {
		return startGrid;
	}

	public void setStartGrid(ArrayList<SudokuCell> grid) {
		startGrid = grid;
	}

	public int getHideCells() {
		int cont = 0;
		for (SudokuCell c : sudokuCells) {
			if(c.isHide())
				cont++;
		}
		return cont;
	}

	public SudokuCell getSelectedCell() {
		return selectedCell;
	}

	public void setSelectedCell(SudokuCell cell) 
	{
		this.selectedCell.setRow(cell.getRow());
		this.selectedCell.setColumn(cell.getColumn());
		this.selectedCell.setAssignedValue(cell.getAssignedValue());
	}

	public boolean removeContent(SudokuCell selectedCell) 
	{
		if(!selectedCell.isHide()) 
		{
			for(SudokuCell cell : startGrid) 
			{			
				if(cell.getRow() == selectedCell.getRow() && 
						cell.getColumn() == selectedCell.getColumn() &&
							!cell.isHide()) {
					System.out.println("presente nello schema iniziale");
					this.selectedCell = new SudokuCell();
					return false;
				}
			}
			for(SudokuCell cell : sudokuCells) 
			{
				if(cell.getRow() == selectedCell.getRow() && 
						cell.getColumn() == selectedCell.getColumn()) {
					System.out.println("Rimossa!");
					cell.removeContent();
					this.selectedCell = new SudokuCell();
					return true;
				}
			}
		}
		return false;
	}

	public boolean solution() 
	{
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for(SudokuCell sudokuCell : startGrid) 
		{
			if(!sudokuCell.isHide()) 
			{
				Cell cell = new Cell(sudokuCell.getRow(), sudokuCell.getColumn(), sudokuCell.getAssignedValue());
				cells.add(cell);
			}
		}
		ArrayList<ArrayList<Cell>> solutions = generator.solveSudoku(cells);
		boolean rightSolution = true;
		for(ArrayList<Cell> currentSolution : solutions) 
		{
			for(Cell cell : currentSolution) 
			{
				for(SudokuCell finalCell : sudokuCells) 
				{
					if(cell.getRow() == finalCell.getRow() &&
							cell.getColumn() == finalCell.getColumn() &&
								cell.getValue() != finalCell.getValue()) {
									rightSolution = false;
									break;
					}
				}
			}
			if(rightSolution)
				return true;
		}
		return false;
		
	}

}
