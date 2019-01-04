package tmp;

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
		if(this.value > 0) {
			setText(Integer.toString(value));
		}
		setScaleX(2.5);
		setScaleY(1.5);
	}
}
