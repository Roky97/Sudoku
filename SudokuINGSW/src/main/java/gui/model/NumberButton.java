package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings("restriction")
public class NumberButton extends Button {

	private int value;
	private int cont;
	
	public NumberButton(int val) {
		setValue(val);
		setCont(9);
		setText(String.valueOf(val));
		setScaleX(1.2);
		setScaleY(1.2);
	}
	
	public void emptyColor() {
		setStyle("-fx-background-color: Red;" +
				"-fx-text-fill: White;");
		setDisable(true);
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
		if(this.cont == 0)
			emptyColor();
		else {
			setStyle("-fx-font-weight: bold;"+
					"-fx-text-fill: Limegreen;");
			setDisable(false);
		}
	}


	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isEmpty() 
	{
		if(this.cont == 0)
			return true;
		
		return false;
	}
}
