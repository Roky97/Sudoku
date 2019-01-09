package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings("restriction")
public class SudokuCell extends Button {

	private int row;
	private int column;
	private int value;
	
	public SudokuCell(int row, int column, int value) {
		setText("  ");
		this.row = row;
		this.column = column;
		this.value = value;
//	    setStyle("-fx-font: 8 arial;");
		setScaleX(1.2);
		setScaleY(1.2);
	}
	
	public void showContent() {
		setText(Integer.toString(value));
	}
	
	
}
