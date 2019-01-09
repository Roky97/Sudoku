package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings("restriction")
public class SudokuCell extends Button {

	private int row;
	private int column;
	private int value;
	
	public SudokuCell(int row, int column, int value) {
		this.row = row;
		this.column = column;
		this.value = value;
	    setStyle("-fx-font: 8 arial;");
		setScaleX(2.1);
		setScaleY(1.5);
	}
	
	public void showContent() {
		setText(Integer.toString(value));
	}
	
	
}
