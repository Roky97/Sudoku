package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings("restriction")
public class SudokuCell extends Button {

	private int row;
	private int column;
	private int value;
	
	public SudokuCell(int row, int column, int value) {
		hideContent();
		this.setRow(row);
		this.setColumn(column);
		this.value = value;
		setScaleX(1.2);
		setScaleY(1.2);
	}
	
	public void showContent() {
		setText(Integer.toString(value));
	}
	public void hideContent() {
		setText("  ");
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
}
