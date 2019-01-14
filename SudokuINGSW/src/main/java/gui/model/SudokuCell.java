package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings({ "restriction", "serial" })
public class SudokuCell extends Button implements java.io.Serializable {

	private int row;
	private int column;
	private int value;
	private boolean hide;
	
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
		hide = false;
	}
	public void hideContent() {
		setText("  ");
		hide = true;
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

	public boolean isHide() {return hide;}
	@Override
	public String toString() {
		return "(" + row + "," + column + "," + "value" + ")";
	}
}
