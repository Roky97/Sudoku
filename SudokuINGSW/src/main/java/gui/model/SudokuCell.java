package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings({ "restriction", "serial" })
public class SudokuCell extends Button implements java.io.Serializable {

	private int row;
	private int column;
	
	private int value;
	
	private boolean hide;
	
	private int assignedValue;
	
	public SudokuCell(int row, int column, int value) 
	{
		hideContent();
		this.setRow(row);
		this.setColumn(column);
		this.value = value;
		setScale(1.2,1.2);
	}
	
	public SudokuCell() {
		hideContent();
	}

	public SudokuCell(SudokuCell sudokuCell) {
		this.assignedValue = sudokuCell.getAssignedValue();
		this.column = sudokuCell.getColumn();
		this.row = sudokuCell.getRow();
		this.value = sudokuCell.getValue();
		if(sudokuCell.isHide()) {
			this.hide = true;
			hideContent();
		}else {
			setAssignedValue(assignedValue);
		}
	}

	public void showContent() {
		setText(Integer.toString(value));
		assignedValue = value;
		hide = false;
	}
	
	public void hideContent() {
		setText("  ");
		hide = true;
	}

	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }

	public int getColumn() { return column; }
	public void setColumn(int column) { this.column = column; }

	public void setValue(int value) { this.value = value; }
	public int getValue() { return value; }

	public boolean isHide() {return hide;}
	
	public void setAssignedValue(int assignedValue) 
	{
		this.assignedValue = assignedValue;
		if(this.assignedValue > 0)
		{
			showContent(assignedValue);
		}
	}

	private void showContent(int assignedValue) {
		setText(Integer.toString(assignedValue));
		hide = false;
	}

	public int getAssignedValue() { return assignedValue; }
	
	
	public void highlightCell() {
		setStyle(getStyle() + "\n-fx-background-color: Red;");
	}
	
	public boolean isHighlighted() 
	{
		if(getStyle().contains("-fx-background-color: Red;"))
			return true;
		return false;
	}

	public void removeHiglight() 
	{
		if(getStyle().contains("-fx-background-color: Red;")) 
		{
			setStyle(getStyle().replaceAll("-fx-background-color: Red;", " "));
		}
	}
	
	public void removeContent() {
		assignedValue = 0;
		hideContent();
	}

	public void setScale(double x, double y) {
		this.setScaleX(1.2);
		this.setScaleY(1.2);
	}

	public void setLayout(double x, double y) {
		setLayoutX(x);
		setLayoutY(y);
	}
	
	public boolean checkPosition(double x, double y) {
		if(row == x && column == y)
			return true;
		return false;
	}

	public void setStartFont() {
		setStyle("-fx-font-weight: bold;");
	}
	
	@Override
	public String toString() {
		return "(" + row + "," + column + "," + assignedValue + ") " + "value:" + value;
	}
	
//////////////////MEMENTO/////////////////////////
//	private SudokuCell state;
//
//	public void setState(SudokuCell cell) {
//		System.out.println("internal state of cell is changed");
//		this.state = new SudokuCell(cell.getColumn(), cell.getRow(), cell.getAssignedValue());
//	}
//	
//	public SudokuCell getState() {
//		return new SudokuCell(state);
//	}
//	
//	public void getStateFromMemento(SudokuCell cell) {
//		state = cell.getState();
//	}
/////////////////////////////////////////////////
}
