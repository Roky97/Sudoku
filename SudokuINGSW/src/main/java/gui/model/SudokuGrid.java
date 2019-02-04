package gui.model;

import java.util.ArrayList;

public class SudokuGrid {

	private SudokuGrid state;
	private ArrayList<SudokuCell> grid;
	
	public SudokuGrid() {
		grid = new ArrayList<SudokuCell>();
	}
	
	public SudokuGrid(ArrayList<SudokuCell> cells) 
	{
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

	public void setCells(ArrayList<SudokuCell> startGrid) {
		this.grid = startGrid;
	}
	
	public void add(SudokuCell sudokuCell) {
		this.grid.add(sudokuCell);
	}

	//MEMENTO
	public void setState(SudokuGrid cells) 
	{
		System.out.println("internal state of grid has changed");
		ArrayList<SudokuCell> sudokuCells = new ArrayList<>();
		for(SudokuCell cell : cells.getGrid()) 
		{
			SudokuCell tmp = new SudokuCell(cell.getRow(), cell.getColumn(), cell.getValue());
			if(!cell.isHide())
				tmp.setAssignedValue(cell.getAssignedValue());
			sudokuCells.add(tmp);
		}
		state = new SudokuGrid(sudokuCells);
	}

	public SudokuGrid getState() {
		return new SudokuGrid(state);
	}

	public void getStateFromMemento(SudokuGrid cells) {
		state = cells.getState();
	}

	@Override
	public String toString() {
		String s = "";
		int cont = 0;
		for(SudokuCell c : grid) {
			if(cont == 9 || cont == 18 || cont == 27 || cont == 36 || cont == 45 || cont == 54 || cont == 63 || cont == 72)
				s += "\n";
			s += " " + String.valueOf(c.getAssignedValue());
			cont++;
		}
		return s;
	}
	
}
