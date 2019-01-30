package logic.ai;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import gui.model.DIFFICULTY;
import gui.model.SudokuCell;

public class GameManager {


	private SudokuGenerator generator; //Mediante un suo metodo interno viene creata una matrice sudoku che rispecchia la difficoltà passata.

	private ArrayList<Cell> grid; //ArrayList di celle che rappresentano le istanze sulle quali viene applicata la logica DLV.
	
	private DIFFICULTY difficulty; //In base ad essa il generator genera una matrice su cui giocare
	
	private ArrayList<SudokuCell> sudokuCells; //ArrayList di bottoni che possiedono delle coordinate(X,Y) ed un valore.
	
	private ArrayList<SudokuCell> startGrid; //Presumo sia la griglia appena generata che utilizziamo per ricominciare la stessa partita.
	
	private int value;//Rappresenta uno dei numeri da 1 a 9 che vogliamo porre in una cella.
	private ArrayList<Point> sameValue; //ArrayList di punti che contiene le coord delle celle che hanno lo stesso valore di un numero che immettiamo e si trovano nella stessa riga,colonna o sottomatrice.
	private SudokuCell selectedCell; //Rappresente la cella sulla quale clicchiamoo.
	
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
	
	
	public void generateSudoku() //Funzione che genera lo schema sudoku su cui giocare.
	{
		if(generator.generateSudoku()) 
		{
			sudokuCells.clear();
			grid.clear();
			grid = generator.getGrid(); //Imposta la griglia di celle dalla quale poi impostiamo la griglia di gioco.
		}
	}
	
	public void selectedValue(int value) { this.setValue(value);}
	public void setValue(int value) { this.value = value;}
	public int getValue() { return this.value;}
	
	public boolean setSudokuCellValue(SudokuCell sudokuCell, int value)//Con questo metodo proviamo a porre un valore in una determinata cella.
	{
		if(sudokuCell.isHide() && value != 0) 
		{
			sameValue.clear();
			int index = sudokuCells.indexOf(sudokuCell);
			return (checkValue(sudokuCells.get(index), value)); //Controlliamo se è possibile porre il valore all'interno di una determinata cella.
		}
		return false;
	}
	


	private boolean checkValue(SudokuCell sudokuCell, int value)//Controlliamo che il valore inserito nella cella rispetti le regole del gioco
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

	private boolean checkOnRowAndColumn(int x, int y, int value) //Controlliamo che il valore che vogliamo immettere non sia presente nella stessa riga e colonna.
	{
		boolean insert = true;

		for(SudokuCell cell : sudokuCells) 
		{
			if(cell.getColumn() == y) 
			{
				if(cell.getRow() != x && cell.getAssignedValue() == value && !cell.isHide())
				{
					sameValue.add(new Point(cell.getRow(),y)); //Aggiungiamo a sameValue le coord che possiedono gli stessi valori nella stessa colonna.
					insert = false;
				}
			}
			if(cell.getRow() == x) 
			{
				if(cell.getColumn() != y && cell.getAssignedValue() == value && !cell.isHide())
				{
					sameValue.add(new Point(x,cell.getColumn())); //Aggiungiamo a sameValue le coord che possiedono gli stessi valori nella stessa riga.
					insert = false;
				}
			}
		}
		return insert;
	}

	private boolean checkOnSubMatrix(int r, int c, int x, int y, int value) //Controlliamo che il valore che vogliamo immettere non sia presente nella sottomatrice.
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
								this.sameValue.add(new Point(cell.getRow(), cell.getColumn()));//Aggiungiamo a sameValue le coord che possiedono gli stessi valori nella stessa sottomatrice.
								insert = false;
							}
						}
					}
				}
			}
		}
		return insert;
	}
		
	public ArrayList<Point> getCellWithSameValue() { //Restituiamo i punti con lo stesso valore per poterli colorare
		return sameValue;
	}

	public void saveGame(DIFFICULTY difficulty, ArrayList<SudokuCell> sudokuCells) //Salviamo il gioco (per poterlo ripristinare forse)
	{
	      try {
	          FileOutputStream fileOut = new FileOutputStream("saves/game.ser");
	          ObjectOutputStream out = new ObjectOutputStream(fileOut);
	          out.writeObject(difficulty);
	          out.writeObject(sudokuCells);
	          out.close();
	          fileOut.close();
	       } catch (IOException i) {
	          i.printStackTrace();
	       }
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public boolean loadGame()  //Carichiamo il gioco salvato in precedenza
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
	
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	
	/////////////
	//SET E GET//
	/////////////
	
	public DIFFICULTY getDifficulty() { return this.difficulty; }
	public void setDifficulty(DIFFICULTY diff) { this.difficulty = diff; }

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

	public int getHideCells() { //Restituisce il numero di celle nascoste 
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

	public boolean removeContent(SudokuCell selectedCell) //Rimuove il contenuto da una cella selezionata nel caso in cui premiamo su delete
	{
		if(!selectedCell.isHide()) 
		{
			for(SudokuCell cell : startGrid) 
			{			
				if(cell.getRow() == selectedCell.getRow() && 
						cell.getColumn() == selectedCell.getColumn() &&
							!cell.isHide()) {
					this.selectedCell = new SudokuCell();
					return false;
				}
			}
			for(SudokuCell cell : sudokuCells) 
			{
				if(cell.getRow() == selectedCell.getRow() && 
						cell.getColumn() == selectedCell.getColumn()) {
					cell.removeContent();
					this.selectedCell = new SudokuCell();
					return true;
				}
			}
		}
		return false;
	}
	
	public int computeVisibleCells() //In base alla difficoltà decidiamo il numero di celle non nascoste nella griglia di gioco
	{
		Random rand = new Random();
		switch (difficulty)
		{
			case EASY:
				return (rand.nextInt(80-79)+79);
			case NORMAL:
				return (rand.nextInt(40-35)+35);
			case HARD:
				return (rand.nextInt(27-25)+25);
				
			default:
				return 0;
		}
	}

	public void clearStartGrid() { 
		startGrid = new ArrayList<SudokuCell>();
	}

	public boolean getSolution(ArrayList<Cell> cells) 
	{
		grid = cells;
		boolean hasSolution = generator.solveSudoku(grid); 
		grid = generator.getGrid();
		return hasSolution;
	}

	public ArrayList<SudokuCell> parseToSudokuCells(ArrayList<Cell> cells) { //Dalle istanze cell ricavate dal generator ci formiamo la griglia di bottoni sudokuCells.
		ArrayList<SudokuCell> sudokuCells = new ArrayList<SudokuCell>();
		for(Cell cell : cells) {
			SudokuCell sudokuCell = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
			sudokuCell.showContent();
			sudokuCells.add(sudokuCell);
		}
		return sudokuCells;
	}

	public ArrayList<Cell> parseToCell(ArrayList<SudokuCell> sudokuCells)
	{
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for(SudokuCell sudokuCell : sudokuCells) 
		{
			if(sudokuCell.getValue() != 0) {
				Cell cell = new Cell(sudokuCell.getRow(), sudokuCell.getColumn(), sudokuCell.getValue());
				cells.add(cell);
			}
		}
		return cells;
	}
}
