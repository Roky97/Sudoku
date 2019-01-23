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

	private SudokuGenerator generator;
	
	private DIFFICULTY difficulty;
	private ArrayList<Cell> grid;
	private int value;
	private ArrayList<SudokuCell> sudokuCells;
	private ArrayList<Point> sameValue;
	
	public GameManager() 
	{
		setValue(0);
		generator = new SudokuGenerator();
		grid = new ArrayList<Cell>();
		sudokuCells = new ArrayList<SudokuCell>();
		sameValue = new ArrayList<Point>();
	}
	
	public void generateSudoku() 
	{
		if(generator.generateSudoku()) 
		{
			grid.clear();
			grid = generator.getGrid();
		}
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
	          System.out.printf("Serialized data is saved in saves/game.ser");
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

	public DIFFICULTY getDifficulty() {
		return this.difficulty;
	}

	public ArrayList<SudokuCell> getSudokuCells() {
		return this.sudokuCells;
	}

	public ArrayList<Cell> getGrid() { return grid; }

	public void selectValue(int value) {
		this.setValue(value);
		System.out.print(" GM " + this.value);
		System.out.println();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setSudokuCells(ArrayList<SudokuCell> sudokuCells) {
		System.out.println("GM sudoku cells aggiornate");
		this.sudokuCells = sudokuCells;
	}

	public boolean setSudokuCellValue(SudokuCell sudokuCell, int value) 
	{
		int index = sudokuCells.indexOf(sudokuCell);
		return (checkValue(sudokuCells.get(index), value));
	}

	private boolean checkValue(SudokuCell sudokuCell, int value) 
	{
		sameValue.clear();
		
		int x = sudokuCell.getRow();
		int y = sudokuCell.getColumn();
		
		System.out.println("SELEZIONATA CELLA(" + x + "," + y + ")");

		int r = 0;
		int c = 0;
		
		if(y >= 3 && y <= 5) 
			c = 3;
		if(y >= 6)
			c = 6;
		
		if(x <= 2)
		{
			if(checkOnSubMatrix(r,c,x,y,value))
				return checkOnRowAndColumn(x,y,value);
		}
		else if(x >= 3 && x <= 5) 
		{
			r = 3;
			
			if(checkOnSubMatrix(r,c,x,y,value))
				return checkOnRowAndColumn(x,y,value);
		}
		else {
			r = 6;
			
			if(checkOnSubMatrix(r,c,x,y,value))
				return checkOnRowAndColumn(x,y,value);
		}
		
		return false;
	}

	private boolean checkOnRowAndColumn(int x, int y, int value) 
	{
		System.out.println("- CONTROLLO RIGHE E COLONNE");
		boolean insert = true;
		for(SudokuCell cell : sudokuCells) 
		{
			if(cell.getColumn() == y) 
			{
				if(cell.getRow() != x && cell.getValue() == value && !cell.isHide())
				{
					System.out.println("cella(" + cell.getRow() + "," + y + "*)");
					sameValue.add(new Point(cell.getRow(),y));
					insert = false;
				}
			}
			if(cell.getRow() == x) 
			{
				if(cell.getColumn() != y && cell.getValue() == value && !cell.isHide())
				{
					System.out.println("cella(" + x + "," + cell.getColumn() + ")");
					sameValue.add(new Point(x,cell.getColumn()));
					insert = false;
				}
			}
		}
		return insert;
		
	}

	private boolean checkOnSubMatrix(int r, int c, int x, int y, int value) 
	{
		System.out.println("- CONTROLLO LA SOTTOMATRICE");
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
							if(cell.getValue() == value) 
							{
								System.out.println("cella(" + i + "," + j + ")");
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
	
	public ArrayList<Point> getCellWithSameValue() 
	{
		return sameValue;
	}

}
