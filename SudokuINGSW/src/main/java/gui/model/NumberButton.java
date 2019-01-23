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
		setStyle("-fx-background-color: Red");
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
	}


	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
