package logic.ai;

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
	private ArrayList<Cell> grid;
	
	private DIFFICULTY difficulty;
	private ArrayList<SudokuCell> sudokuCells;
	
	public GameManager() 
	{
		generator = new SudokuGenerator();
		grid = new ArrayList<Cell>();
		sudokuCells = new ArrayList<SudokuCell>();
	}
	
	public void generateSudoku() 
	{
		if(generator.generateSudoku()) 
		{
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

	@SuppressWarnings("unchecked")
	public boolean loadGame() 
	{
		try {
			FileInputStream fileIn;
			fileIn = new FileInputStream("saves/game.ser");
//manca la condizione per capire se il file è vuoto
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.difficulty = (DIFFICULTY) in.readObject();
			this.sudokuCells = (ArrayList<SudokuCell>) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("Load successfull");

			return true;
			
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

}
