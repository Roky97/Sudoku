package gui.model;

import java.util.ArrayList;

public class SudokuGrid {

	private SudokuGrid state;
	private ArrayList<SudokuCell> grid;
	
	public SudokuGrid() {
		grid = new ArrayList<SudokuCell>();
	}
	
	public SudokuGrid(ArrayList<SudokuCell> cells) {
		grid = new ArrayList<SudokuCell>();
		grid = cells;
	}
	
	public SudokuGrid(SudokuGrid newGrid) {
		grid = newGrid.getGrid();
	}

	private ArrayList<SudokuCell> getGrid() {
		return this.grid;
	}

	public int size() {
		return grid.size();
	}
	
	public ArrayList<SudokuCell> getCells() {
		return grid;
	}
	
	//MEMENTO
	public void setState(SudokuGrid cells) 
	{
		System.out.println("internal state of grid is changed");
		this.state = new SudokuGrid(cells);
	}

	public SudokuGrid getState() {
		return new SudokuGrid(state);
	}

	public void getStateFromMemento(SudokuGrid cells) {
		state = cells.getState();
	}

	public void setCells(ArrayList<SudokuCell> startGrid) {
		this.grid = startGrid;
	}

	public void add(SudokuCell sudokuCell) {
		this.grid.add(sudokuCell);
	}

	
}
