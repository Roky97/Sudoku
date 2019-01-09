package tmp;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("cell")
public class Cell {

	@Param(0)
	private int row;
	@Param(1)
	private int column;
	@Param(2)
	private int value;
	
	public Cell() {}
	
	public Cell(int x, int y, int v) 
	{
		this.row = x;
		this.column = y;
		this.value = v;
	}

	public void setRow(int row) {this.row = row;}
	public int getRow() {return row;}
	
	public void setColumn(int column) {this.column = column;}
	public int getColumn() {return column;}
	
	public void setValue(int value) {this.value = value;}
	public int getValue() {return value;}
}
